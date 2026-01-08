import { useState, useEffect } from "react";
import { collection, onSnapshot } from "firebase/firestore";
import { db } from "@/firebase";

// Estruturas de dados para o gráfico
export interface ChartProduct {
  name: string;
  value: number;
  percentage: number;
}

export interface NeededProduct {
  name: string;
  urgency: "high" | "medium" | "low";
}

export interface CategoryData {
  title: string;
  products: ChartProduct[];
  neededProducts: NeededProduct[];
  colors: string[];
  total: number; // Adicionei a tipagem que faltava
}

export const useStockData = () => {
  const [stockData, setStockData] = useState<Record<string, CategoryData>>({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 1. Ouvir a coleção de PRODUTOS (bens)
    // Mapeia ID -> Dados (ex: 'bem_arroz' -> { name: 'Arroz', type: 'FOOD' })
    const unsubProducts = onSnapshot(collection(db, "bens"), (productsSnapshot) => {
      const productsMap: Record<string, { name: string; type: string }> = {};
      
      productsSnapshot.docs.forEach((doc) => {
        const data = doc.data();
        productsMap[doc.id] = { 
            name: data.nome || "Produto sem nome", 
            type: data.tipo || "OUTROS" 
        };
      });

      // 2. Ouvir a coleção de INVENTÁRIO (bens_inventario)
      const unsubInventory = onSnapshot(collection(db, "bens_inventario"), (inventorySnapshot) => {
        const tempCounts: Record<string, number> = {};

        // Somar quantidades por ProductID
        inventorySnapshot.docs.forEach((doc) => {
          const data = doc.data();
          // Garante que usamos o productId que liga ao documento em 'bens'
          const pId = data.productId; 
          const qty = Number(data.quantity) || 0;

          if (pId) {
             tempCounts[pId] = (tempCounts[pId] || 0) + qty;
          }
        });

        // 3. Preparar a estrutura base
        const processedData: Record<string, CategoryData> = {
          alimentares: { 
            title: "Bens Alimentares", 
            products: [], neededProducts: [], total: 0, 
            colors: ["#00a67e", "#00d4a0", "#4de3b8", "#7ee9c9", "#a8efd9"] 
          },
          higiene: { 
            title: "Produtos de Higiene", 
            products: [], neededProducts: [], total: 0, 
            colors: ["#0891b2", "#06b6d4", "#22d3ee", "#67e8f9", "#a5f3fc"] 
          },
          casa: { 
            title: "Produtos de Casa", 
            products: [], neededProducts: [], total: 0, 
            colors: ["#f97316", "#fb923c", "#fdba74", "#fed7aa", "#ffedd5"] 
          },
          outros: { 
            title: "Outros", 
            products: [], neededProducts: [], total: 0, 
            colors: ["#8884d8", "#83a6ed", "#8dd1e1", "#82ca9d", "#a4de6c"] 
          }
        };

        // 4. Preencher os dados
        Object.keys(productsMap).forEach((pId) => {
          const info = productsMap[pId];
          const qty = tempCounts[pId] || 0;
          
          // LÓGICA CORRIGIDA AQUI:
          // Normalizamos tudo para Maiúsculas para bater certo com o banco
          const tipoUpper = (info.type || "").toUpperCase(); 
          let catKey = "outros";
          
          if (tipoUpper.includes("FOOD") || tipoUpper === "ALIMENTAR") catKey = "alimentares";
          else if (tipoUpper.includes("HYGIENE") || tipoUpper === "HIGIENE") catKey = "higiene";
          else if (tipoUpper.includes("CLEANING") || tipoUpper === "LIMPEZA" || tipoUpper === "CASA") catKey = "casa";

          // Adicionar produto
          processedData[catKey].products.push({
            name: info.name,
            value: qty,
            percentage: 0 // Será calculado abaixo
          });

          // Somar ao total da categoria
          processedData[catKey].total += qty;

          // Definir Urgência (Exemplo: < 10 é baixo, 0 é crítico)
          if (qty < 10) {
            processedData[catKey].neededProducts.push({
              name: info.name,
              urgency: qty === 0 ? "high" : "medium"
            });
          }
        });

        // 5. Cálculos Finais (Percentagens e Ordenação)
        Object.keys(processedData).forEach(key => {
          const category = processedData[key];
          const totalCategory = category.total || 1; 

          // Ordenar produtos (maior quantidade primeiro) e calcular %
          category.products = category.products
            .sort((a, b) => b.value - a.value)
            .map((p) => ({
              ...p,
              percentage: Math.round((p.value / totalCategory) * 100)
            }));
            
          // Se não houver nada em falta, mensagem padrão
          if (category.neededProducts.length === 0) {
             category.neededProducts.push({ name: "Stock Estável", urgency: "low" });
          }
        });

        setStockData(processedData);
        setLoading(false);
      });

      return () => unsubInventory();
    });

    return () => unsubProducts();
  }, []);

  return { stockData, loading };
};
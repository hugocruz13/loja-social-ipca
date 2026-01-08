import { useState } from "react";
import Navigation from "@/components/Navigation";
import Hero from "@/components/Hero";
import StockOverview from "@/components/StockOverview";
import ProductChart from "@/components/ProductChart";
import Campaigns from "@/components/Campaigns";
import Donations from "@/components/Donations";
import { useStockData } from "@/hooks/useStockData";

const Index = () => {
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  
  // Usar o Hook para obter dados reais
  const { stockData, loading } = useStockData();

  return (
    <div className="min-h-screen bg-background">
      <Navigation />
      <Hero />
      <div id="stock">
        <StockOverview onCategoryClick={setSelectedCategory} />
      </div>
      
      {/* Passar os dados reais para o componente */}
      <ProductChart 
        categoryId={selectedCategory} 
        onClose={() => setSelectedCategory(null)} 
        realData={stockData} 
      />
      
      <div id="campanhas">
        <Campaigns />
      </div>
      <Donations />
      <footer className="border-t border-border bg-card py-8 px-6">
        <div className="container mx-auto max-w-6xl text-center">
          <p className="text-sm text-muted-foreground">
            © 2026 Loja Social Solidária. Juntos fazemos a diferença.
          </p>
        </div>
      </footer>
    </div>
  );
};

export default Index;
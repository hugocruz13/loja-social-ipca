import { X, AlertCircle, PackageOpen } from "lucide-react";
import { 
  Dialog, 
  DialogContent, 
  DialogTitle, 
  DialogHeader,
  DialogDescription 
} from "@/components/ui/dialog";
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Tooltip,
  Legend
} from "recharts";
import { Badge } from "@/components/ui/badge";
import { CategoryData } from "@/hooks/useStockData";
import { useMemo } from "react";

interface ProductChartProps {
  categoryId: string | null;
  onClose: () => void;
  realData: Record<string, CategoryData>;
}

const ProductChart = ({ categoryId, onClose, realData }: ProductChartProps) => {
  if (!categoryId) return null;

  const data = realData[categoryId];
  
  // Hook useMemo para calcular o agrupamento apenas quando os dados mudam
  const { chartData, otherProductNames, hasStock } = useMemo(() => {
    if (!data || data.total === 0) return { chartData: [], otherProductNames: [], hasStock: false };

    const mainProducts: any[] = [];
    const smallProducts: any[] = [];

    // Separar produtos maiores e menores que 10%
    data.products.forEach(p => {
      if (p.percentage < 10) {
        smallProducts.push(p);
      } else {
        mainProducts.push(p);
      }
    });

    // Se houver produtos pequenos, criar a categoria "Outros"
    if (smallProducts.length > 0) {
      const othersTotalValue = smallProducts.reduce((sum, p) => sum + p.value, 0);
      const othersTotalPercentage = smallProducts.reduce((sum, p) => sum + p.percentage, 0);
      
      mainProducts.push({
        name: "Outros",
        value: othersTotalValue,
        percentage: othersTotalPercentage,
        isOthers: true // Flag para identificarmos depois
      });
    }

    return { 
      chartData: mainProducts,
      // Guardar os nomes dos produtos pequenos para exibir em baixo
      otherProductNames: smallProducts.map(p => p.name),
      hasStock: true
    };
  }, [data]);

  if (!data) return null;

  const getUrgencyColor = (urgency: string) => {
    switch (urgency) {
      case "high": return "bg-red-500";
      case "medium": return "bg-orange-500";
      case "low": return "bg-yellow-500";
      default: return "bg-gray-500";
    }
  };

  return (
    <Dialog open={!!categoryId} onOpenChange={onClose}>
      <DialogContent className="max-w-5xl max-h-[90vh] overflow-y-auto">
        
        <DialogHeader className="mb-4">
            <div className="flex justify-between items-start">
                <div>
                    <DialogTitle className="text-3xl font-bold text-foreground">
                        {data.title}
                    </DialogTitle>
                    <DialogDescription className="text-sm text-muted-foreground mt-1">
                        Distribuição atual do stock e necessidades
                    </DialogDescription>
                </div>
            </div>
        </DialogHeader>

        {!hasStock ? (
             <div className="flex flex-col items-center justify-center py-16 text-center space-y-4 border-2 border-dashed border-muted rounded-lg bg-muted/10">
                <div className="bg-muted p-4 rounded-full">
                    <PackageOpen className="h-10 w-10 text-muted-foreground" />
                </div>
                <div>
                    <h3 className="text-xl font-semibold text-foreground">Sem Stock Registado</h3>
                    <p className="text-muted-foreground max-w-sm mt-2">
                        Não existem produtos registados nesta categoria ou a quantidade em stock é zero.
                    </p>
                </div>
             </div>
        ) : (
            <div className="grid md:grid-cols-2 gap-8">
            
            {/* --- COLUNA ESQUERDA: GRÁFICO --- */}
            <div>
                <h3 className="text-lg font-semibold text-foreground mb-4">
                   Composição do Stock
                </h3>
                <div className="h-[350px] w-full relative">
                <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                    <Pie
                        data={chartData}
                        cx="50%"
                        cy="50%"
                        outerRadius={90} 
                        fill="#8884d8"
                        dataKey="value"
                        label={({ name, percentage }) => `${name} (${percentage}%)`} 
                    >
                        {chartData.map((entry, index) => (
                          <Cell 
                            key={`cell-${index}`} 
                            // Se for "Outros", usa uma cor cinza (ou a última da lista), senão usa a cor da categoria
                            fill={entry.name === "Outros" ? "#94a3b8" : data.colors[index % data.colors.length]} 
                          />
                        ))}
                    </Pie>
                    <Tooltip 
                        formatter={(value: number) => [`${value} Unidades`, "Quantidade"]}
                        contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                    />
                    </PieChart>
                </ResponsiveContainer>
                </div>

                {/* --- LEGENDA ESPECIAL PARA "OUTROS" --- */}
                {otherProductNames.length > 0 && (
                  <div className="mt-4 p-4 bg-muted/30 rounded-lg border border-border text-sm">
                    <div className="flex items-center gap-2 mb-2 font-medium">
                      {/* Bolinha Cinza igual à fatia do gráfico */}
                      <span className="w-3 h-3 rounded-full bg-[#94a3b8]"></span>
                      <span>Outros ({"<"} 10%)</span>
                    </div>
                    <p className="text-muted-foreground leading-relaxed">
                      {otherProductNames.join(", ")}
                    </p>
                  </div>
                )}
            </div>

            {/* --- COLUNA DIREITA: PRODUTOS NECESSÁRIOS --- */}
            <div>
                <div className="flex items-center gap-2 mb-4">
                <AlertCircle className="h-5 w-5 text-destructive" />
                <h3 className="text-lg font-semibold text-foreground">
                    Produtos Necessários
                </h3>
                </div>
                
                <div className="space-y-3">
                <p className="text-sm text-muted-foreground mb-4">
                    Items com baixo stock que precisamos urgentemente
                </p>
                
                {data.neededProducts.length > 0 ? (
                    data.neededProducts.map((product, index) => (
                        <div 
                        key={index}
                        className="flex items-center justify-between p-4 rounded-lg bg-muted/50 hover:bg-muted transition-colors"
                        >
                        <div className="flex items-center gap-3">
                            <span className={`h-2 w-2 rounded-full ${getUrgencyColor(product.urgency)}`} />
                            <span className="font-medium text-foreground">{product.name}</span>
                        </div>
                        <Badge 
                            variant={product.urgency === "high" ? "destructive" : "secondary"}
                            className="capitalize"
                        >
                            {product.urgency === "high" ? "Urgente" : product.urgency === "medium" ? "Médio" : "Baixo"}
                        </Badge>
                        </div>
                    ))
                ) : (
                    <div className="p-4 rounded-lg bg-green-500/10 border border-green-500/20 text-green-700 dark:text-green-400">
                        <p className="text-sm font-medium">Stock estável. Não há produtos em falta nesta categoria.</p>
                    </div>
                )}
                </div>
                
                <div className="mt-6 p-4 rounded-lg bg-primary/10 border border-primary/20">
                <p className="text-sm text-foreground">
                    <span className="font-semibold">Nota:</span> A urgência é determinada com base nos níveis de stock atual.
                </p>
                </div>
            </div>
            </div>
        )}
      </DialogContent>
    </Dialog>
  );
};

export default ProductChart;
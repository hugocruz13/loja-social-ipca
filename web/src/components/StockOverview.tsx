import { ShoppingBasket, Sparkles, Home } from "lucide-react";
import { Card } from "@/components/ui/card";

interface StockCategory {
  id: string;
  title: string;
  icon: React.ReactNode;
  percentage: number;
  color: string;
  bgColor: string;
}

interface StockOverviewProps {
  onCategoryClick: (categoryId: string) => void;
}

const StockOverview = ({ onCategoryClick }: StockOverviewProps) => {
  const categories: StockCategory[] = [
    {
      id: "alimentares",
      title: "Bens Alimentares",
      icon: <ShoppingBasket className="h-8 w-8" />,
      percentage: 75,
      color: "text-primary",
      bgColor: "bg-primary/10",
    },
    {
      id: "higiene",
      title: "Produtos de Higiene",
      icon: <Sparkles className="h-8 w-8" />,
      percentage: 60,
      color: "text-secondary",
      bgColor: "bg-secondary/10",
    },
    {
      id: "casa",
      title: "Produtos de Casa",
      icon: <Home className="h-8 w-8" />,
      percentage: 45,
      color: "text-accent",
      bgColor: "bg-accent/10",
    },
  ];

  return (
    <section className="py-12 px-6">
      <div className="container mx-auto max-w-6xl">
        <h2 className="mb-8 text-3xl font-bold text-center text-foreground">
          Stock Disponível
        </h2>
        <div className="grid gap-6 md:grid-cols-3">
          {categories.map((category) => (
            <Card
              key={category.id}
              className="group cursor-pointer overflow-hidden border-border bg-card p-8 transition-all hover:shadow-[var(--shadow-hover)] flex flex-col items-center text-center"
              onClick={() => onCategoryClick(category.id)}
            >
              <div className={`mb-6 inline-flex rounded-xl ${category.bgColor} p-6`}>
                <div className={category.color}>{category.icon}</div>
              </div>
              <h3 className="mb-4 text-xl font-semibold text-card-foreground">
                {category.title}
              </h3>
              <p className="mt-4 text-sm text-muted-foreground">
                Clique para ver detalhes
              </p>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};

export default StockOverview;

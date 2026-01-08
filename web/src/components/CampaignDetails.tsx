import { Calendar, MapPin, Package, X } from "lucide-react";
import { Dialog, DialogContent } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

interface Campaign {
  id: string;
  title: string;
  description: string;
  status: "active" | "upcoming";
  type: string; // 1. Adicionado para ler o tipo
  startDate: string;
  endDate: string;
  image: string;
  detailedDescription: string;
  neededItems: string[];
  location: string;
  locationUrl: string;
}

interface CampaignDetailsProps {
  campaign: Campaign | null;
  onClose: () => void;
}

const CampaignDetails = ({ campaign, onClose }: CampaignDetailsProps) => {
  if (!campaign) return null;

  // Variável auxiliar para verificar se mostramos o local
  const showLocation = campaign.type === "Interno" || campaign.type === "Interna";

  return (
    <Dialog open={!!campaign} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <button
          onClick={onClose}
          className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none data-[state=open]:bg-accent data-[state=open]:text-muted-foreground z-10"
        >
          <X className="h-4 w-4" />
          <span className="sr-only">Fechar</span>
        </button>

        <div className="space-y-6">
          <div className="relative h-64 -mx-6 -mt-6 overflow-hidden rounded-t-lg">
            <img 
              src={campaign.image} 
              alt={campaign.title}
              className="h-full w-full object-cover"
            />
            <div className="absolute top-4 right-4">
              <Badge
                variant={campaign.status === "active" ? "default" : "secondary"}
                className={
                  campaign.status === "active"
                    ? "bg-primary text-primary-foreground"
                    : "bg-muted text-muted-foreground"
                }
              >
                {campaign.status === "active" ? "Ativa" : "Em Breve"}
              </Badge>
            </div>
          </div>

          <div>
            <h2 className="text-3xl font-bold text-foreground mb-2">
              {campaign.title}
            </h2>
            <p className="text-muted-foreground">
              {campaign.detailedDescription}
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-3">
              <div className="flex items-start gap-3 p-4 rounded-lg bg-muted/50">
                <Calendar className="h-5 w-5 text-primary mt-0.5" />
                <div>
                  <p className="font-medium text-foreground">Período</p>
                  <p className="text-sm text-muted-foreground">
                    {campaign.startDate} até {campaign.endDate}
                  </p>
                </div>
              </div>

              {/* 2. CONDIÇÃO PARA O BLOCO DE LOCALIZAÇÃO */}
              {showLocation && (
                <div 
                  className="flex items-start gap-3 p-4 rounded-lg bg-muted/50 cursor-pointer hover:bg-muted transition-colors"
                  onClick={() => window.open(campaign.locationUrl, '_blank')}
                >
                  <MapPin className="h-5 w-5 text-secondary mt-0.5" />
                  <div className="flex-1">
                    <p className="font-medium text-foreground">Local</p>
                    <p className="text-sm text-muted-foreground">
                      {campaign.location}
                    </p>
                    <p className="text-xs text-primary mt-1">
                      Clique para abrir no Google Maps
                    </p>
                  </div>
                </div>
              )}
            </div>

            <div className="space-y-3">
              <div className="p-4 rounded-lg bg-muted/50">
                <div className="flex items-center gap-2 mb-3">
                  <Package className="h-5 w-5 text-accent" />
                  <p className="font-medium text-foreground">Itens Necessários</p>
                </div>
                <ul className="space-y-2">
                  {campaign.neededItems.map((item, index) => (
                    <li 
                      key={index}
                      className="text-sm text-muted-foreground flex items-center gap-2"
                    >
                      <span className="h-1.5 w-1.5 rounded-full bg-primary" />
                      {item}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            {/* 3. CONDIÇÃO PARA O BOTÃO VER LOCALIZAÇÃO */}
            {showLocation && (
              <Button 
                className="flex-1"
                onClick={() => window.open(campaign.locationUrl, '_blank')}
              >
                <MapPin className="h-4 w-4 mr-2" />
                Ver Localização
              </Button>
            )}
            
            <Button 
              // Se não mostrar o botão de localização, o botão Fechar ocupa tudo (flex-1)
              // Se mostrar ambos, o Fechar fica normal (flex-none ou padrão)
              className={!showLocation ? "flex-1" : ""}
              variant="outline"
              onClick={onClose}
            >
              Fechar
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default CampaignDetails;
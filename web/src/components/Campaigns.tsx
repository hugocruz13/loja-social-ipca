import { useEffect, useState } from "react";
import { collection, onSnapshot, query, where } from "firebase/firestore";
import { db } from "@/firebase";
import { Calendar, MapPin, ExternalLink } from "lucide-react"; // Adicionei ExternalLink opcional
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import CampaignDetails from "./CampaignDetails";

export interface Campaign {
  id: string;
  title: string;
  description: string;
  status: "active" | "upcoming";
  type: string; // 1. Adicionado o campo Tipo
  startDate: string;
  endDate: string;
  image: string;
  detailedDescription: string;
  neededItems: string[];
  location: string;
  locationUrl: string;
}

const Campaigns = () => {
  const [selectedCampaign, setSelectedCampaign] = useState<string | null>(null);
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const q = query(
      collection(db, "campanhas"),
      where("estado", "in", ["Ativa", "Agendada", "Planeada"]) 
    );

    const unsubscribe = onSnapshot(q, (snapshot) => {
      console.log("Total de campanhas encontradas:", snapshot.size);

      const campaignsData = snapshot.docs.map((doc) => {
        const data = doc.data();
        
        const formatData = (timestamp: number | any) => {
            if (!timestamp) return "";
            const date = typeof timestamp === 'number' ? new Date(timestamp) : timestamp?.toDate ? timestamp.toDate() : new Date();
            return date.toLocaleDateString('pt-PT');
        };

        const start = formatData(data.dataInicio);
        const end = formatData(data.dataFim);

        let statusUI: "active" | "upcoming" = "active";
        if (data.estado === "Agendada" || data.estado === "Planeada") {
            statusUI = "upcoming";
        }

        // Normalizar o tipo para garantir que apanha "Interno" ou "Interna"
        const tipoCampanha = data.tipo || "Externo";

        return {
          id: doc.id,
          title: data.nome || "Sem título",
          description: data.descricao || "",
          status: statusUI,
          type: tipoCampanha, // Guardamos o tipo
          startDate: start,
          endDate: end,
          image: data.imagemUrl || "https://images.unsplash.com/photo-1512389142860-9c449e58a543?w=800&auto=format&fit=crop",
          detailedDescription: data.descricao || "Sem detalhes adicionais.", 
          neededItems: [], 
          location: "Loja Social IPCA", 
          // 2. URL ESPECÍFICO PEDIDO:
          locationUrl: "https://maps.app.goo.gl/szK2H6UJF55uUfAC9",
        } as Campaign;
      });

      setCampaigns(campaignsData);
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  if (loading) {
    return <div className="text-center py-12">A carregar campanhas...</div>;
  }

  return (
    <>
      <section id="campanhas" className="py-12 px-6 bg-muted/30">
        <div className="container mx-auto max-w-6xl">
          <h2 className="mb-8 text-3xl font-bold text-center text-foreground">
            Campanhas Ativas
          </h2>
          
          {campaigns.length === 0 ? (
            <div className="text-center">
                <p className="text-muted-foreground mb-2">Não existem campanhas ativas de momento.</p>
                <p className="text-xs text-muted-foreground">(Verifique se as campanhas no banco têm estado 'Ativa' ou 'Agendada')</p>
            </div>
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {campaigns.map((campaign) => (
                <Card
                  key={campaign.id}
                  className="group cursor-pointer overflow-hidden border-border bg-card transition-all hover:shadow-[var(--shadow-hover)]"
                  onClick={() => setSelectedCampaign(campaign.id)}
                >
                  <div className="relative h-48 overflow-hidden">
                    <img 
                      src={campaign.image} 
                      alt={campaign.title}
                      className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
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
                        {campaign.status === "active" ? "Ativa" : "Agendada"}
                      </Badge>
                    </div>
                  </div>
                  <div className="p-6">
                    <h3 className="mb-2 text-xl font-semibold text-card-foreground">
                      {campaign.title}
                    </h3>
                    <p className="mb-4 text-sm text-muted-foreground line-clamp-2">
                      {campaign.description}
                    </p>
                    <div className="space-y-2 border-t border-border pt-4">
                      <div className="flex items-center gap-2 text-sm">
                        <Calendar className="h-4 w-4 text-primary" />
                        <span className="text-muted-foreground">
                          {campaign.startDate} - {campaign.endDate}
                        </span>
                      </div>
                      
                      {/* 3. LÓGICA DE EXIBIÇÃO DO LOCAL: */}
                      {/* Verifica se o tipo é Interno ou Interna */}
                      {(campaign.type === "Interno" || campaign.type === "Interna") && (
                        <a 
                          href={campaign.locationUrl}
                          target="_blank" 
                          rel="noopener noreferrer"
                          // stopPropagation impede que o clique no mapa abra o modal de detalhes
                          onClick={(e) => e.stopPropagation()} 
                          className="flex items-center gap-2 text-sm hover:underline hover:text-primary transition-colors w-fit"
                          title="Ver localização no Google Maps"
                        >
                          <MapPin className="h-4 w-4 text-secondary" />
                          <span className="text-muted-foreground group-hover/link:text-primary">
                            {campaign.location}
                          </span>
                        </a>
                      )}

                    </div>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>
      </section>
      <CampaignDetails
        campaign={campaigns.find(c => c.id === selectedCampaign) || null}
        onClose={() => setSelectedCampaign(null)}
      />
    </>
  );
};

export default Campaigns;
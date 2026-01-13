import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { MapPin, Phone, Mail, Clock, Loader2, Send } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { addDoc, collection, serverTimestamp } from "firebase/firestore";
import { db } from "@/firebase";

const Donations = () => {
  const { toast } = useToast();
  const [loading, setLoading] = useState(false);
  
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    message: "",
  });

  // Função para validar email (extra opcional, mas recomendada)
  const isValidEmail = (email: string) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // --- 1. VALIDAÇÃO DOS CAMPOS OBRIGATÓRIOS ---
    if (!formData.name.trim() || !formData.email.trim() || !formData.phone.trim()) {
      toast({
        title: "Campos em falta",
        description: "Por favor, preencha o Nome, Email e Telefone.",
        variant: "destructive",
      });
      return;
    }

    // --- 2. VALIDAÇÃO DO TELEMÓVEL (Tamanho) ---
    // Como impedimos letras no handleChange, aqui só validamos o tamanho
    if (formData.phone.length < 9) {
      toast({
        title: "Telefone inválido",
        description: "O número de telefone deve ter 9 dígitos.",
        variant: "destructive",
      });
      return;
    }

    // --- 3. VALIDAÇÃO DE EMAIL ---
    if (!isValidEmail(formData.email)) {
        toast({
            title: "Email inválido",
            description: "Por favor insira um endereço de email válido.",
            variant: "destructive",
        });
        return;
    }

    setLoading(true);

    try {
      const emailHtmlBody = `
        <div style="font-family: Arial, sans-serif; color: #333;">
          <h2 style="color: #00a67e;">Nova Oferta de Doação (Via Portal Web)</h2>
          <p>Recebeu um novo contacto de doação espontânea.</p>
          <hr style="border: 0; border-top: 1px solid #eee;"/>
          <p><strong>Nome do Doador:</strong> ${formData.name}</p>
          <p><strong>Email:</strong> ${formData.email}</p>
          <p><strong>Telefone:</strong> ${formData.phone}</p>
          <p><strong>Mensagem:</strong><br/>${formData.message}</p>
          <br/>
          <p style="font-size: 12px; color: #999;">Este email foi gerado automaticamente pelo formulário do site.</p>
        </div>
      `;

      await addDoc(collection(db, "mail_queue"), {
        to: "a27962@alunos.ipca.pt",
        message: {
          subject: `Nova Doação: ${formData.name}`,
          html: emailHtmlBody,
          replyTo: formData.email, 
          from: "Portal Loja Social <lojasocial@gmail.com>"
        },
        createdAt: serverTimestamp()
      });

      toast({
        title: "Mensagem Enviada!",
        description: "Obrigado pelo seu contacto. Responderemos para o seu email em breve.",
        variant: "default",
        className: "bg-green-500 text-white border-none"
      });

      setFormData({ name: "", email: "", phone: "", message: "" });

    } catch (error) {
      console.error("Erro ao enviar email:", error);
      toast({
        title: "Erro ao enviar",
        description: "Houve um problema técnico. Por favor contacte-nos por telefone.",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;

    // --- LÓGICA ESPECIAL PARA O TELEFONE ---
    if (name === "phone") {
      // 1. Remove tudo o que NÃO for número (regex \D remove não-dígitos)
      const onlyNumbers = value.replace(/\D/g, "");

      // 2. Limita a 9 caracteres (Max 9)
      if (onlyNumbers.length > 9) {
        return; // Não atualiza o estado se passar de 9
      }

      setFormData({ ...formData, [name]: onlyNumbers });
    } else {
      // Comportamento normal para os outros campos
      setFormData({ ...formData, [name]: value });
    }
  };

  return (
    <section id="doacoes" className="py-12 px-6 bg-muted/30">
      <div className="container mx-auto max-w-6xl">
        <h2 className="mb-8 text-3xl font-bold text-center text-foreground">
          Como Doar
        </h2>
        
        <div className="grid gap-8 md:grid-cols-2">
          {/* Lado Esquerdo - Informações Estáticas */}
          <Card className="p-8 bg-card border-border h-fit">
            <h3 className="text-2xl font-semibold mb-6 text-card-foreground">
              Doações Espontâneas
            </h3>
            <div className="space-y-6">
              <div className="flex items-start gap-4">
                <MapPin className="h-6 w-6 text-primary mt-1 flex-shrink-0" />
                <div>
                  <h4 className="font-semibold text-card-foreground mb-1">Localização</h4>
                  <p className="text-muted-foreground">
                    IPCA - Instituto Politécnico do Cávado e do Ave<br />
                    Campus do IPCA, Barcelos
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <Clock className="h-6 w-6 text-primary mt-1 flex-shrink-0" />
                <div>
                  <h4 className="font-semibold text-card-foreground mb-1">Horário</h4>
                  <p className="text-muted-foreground">
                    Segunda a Sexta: 9h00 - 17h00
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <Mail className="h-6 w-6 text-primary mt-1 flex-shrink-0" />
                <div>
                  <h4 className="font-semibold text-card-foreground mb-1">Email</h4>
                  <p className="text-muted-foreground">
                    lojasocial@ipca.pt
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <Phone className="h-6 w-6 text-primary mt-1 flex-shrink-0" />
                <div>
                  <h4 className="font-semibold text-card-foreground mb-1">Telefone</h4>
                  <p className="text-muted-foreground">
                    +351 253 802 500
                  </p>
                </div>
              </div>

              <div className="pt-4 p-4 bg-primary/5 rounded-lg border border-primary/10">
                <p className="text-sm text-muted-foreground">
                  <span className="font-semibold text-primary">Nota Importante:</span> Aceitamos produtos alimentares não perecíveis, produtos de higiene pessoal e do lar.
                </p>
              </div>
            </div>
          </Card>

          {/* Lado Direito - Formulário */}
          <Card className="p-8 bg-card border-border">
            <h3 className="text-2xl font-semibold mb-6 text-card-foreground">
              Solicite Contacto
            </h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <Label htmlFor="name">Nome <span className="text-red-500">*</span></Label>
                <Input
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  // required - Removi o HTML required para usarmos a nossa validação personalizada com Toast
                  className="mt-1"
                  placeholder="O seu nome"
                  disabled={loading}
                />
              </div>

              <div>
                <Label htmlFor="email">Email <span className="text-red-500">*</span></Label>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="mt-1"
                  placeholder="seu.email@exemplo.com"
                  disabled={loading}
                />
              </div>

              <div>
                <Label htmlFor="phone">Telefone <span className="text-red-500">*</span></Label>
                <Input
                  id="phone"
                  name="phone"
                  type="tel"
                  value={formData.phone}
                  onChange={handleChange}
                  className="mt-1"
                  placeholder="9xx xxx xxx"
                  disabled={loading}
                  maxLength={9} // Reforço HTML
                />
                <p className="text-xs text-muted-foreground mt-1">Apenas números (9 dígitos)</p>
              </div>

              <div>
                <Label htmlFor="message">Mensagem (opcional)</Label>
                <Textarea
                  id="message"
                  name="message"
                  value={formData.message}
                  onChange={handleChange}
                  className="mt-1 min-h-[100px]"
                  placeholder="Conte-nos o que gostaria de doar..."
                  disabled={loading}
                />
              </div>

              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    A Enviar...
                  </>
                ) : (
                  <>
                    <Send className="mr-2 h-4 w-4" />
                    Enviar Pedido
                  </>
                )}
              </Button>
            </form>
          </Card>
        </div>
      </div>
    </section>
  );
};

export default Donations;
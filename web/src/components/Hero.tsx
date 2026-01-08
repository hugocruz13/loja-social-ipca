import logoCores from "@/assets/logo-cores.png";
import heroBackground from "@/assets/hero-background.jpg";

const Hero = () => {
  return (
    <header className="relative overflow-hidden py-32 px-6 mt-16">
      {/* Background Image */}
      <div 
        className="absolute inset-0 z-0"
        style={{
          backgroundImage: `url(${heroBackground})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          backgroundRepeat: "no-repeat",
        }}
      >
        <div className="absolute inset-0 bg-gradient-to-r from-background/90 via-background/70 to-background/90" />
      </div>

      {/* Content */}
      <div className="container mx-auto max-w-6xl relative z-10">
        <div className="mb-8 flex justify-center">
          <img 
            src={logoCores} 
            alt="IPCA Serviços de Ação Social - Loja Social" 
            className="h-24 md:h-32 object-contain drop-shadow-lg" 
          />
        </div>
        <div className="text-center">
          <h1 className="mb-4 text-4xl font-bold tracking-tight text-foreground sm:text-5xl md:text-6xl drop-shadow-md">
            Loja Social do IPCA
          </h1>
          <p className="mx-auto max-w-2xl text-lg text-foreground/90 md:text-xl drop-shadow">
            Através da doação de produtos essenciais, fazemos a diferença na vida de quem mais precisa.
            Acompanhe o nosso stock e campanhas ativas.
          </p>
        </div>
      </div>
    </header>
  );
};
export default Hero;
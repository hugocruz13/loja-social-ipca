import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import logoBranco from "@/assets/logo-branco.png";

const Navigation = () => {
  const [isVisible, setIsVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);

  useEffect(() => {
    const controlNavbar = () => {
      const currentScrollY = window.scrollY;
      
      if (currentScrollY < 10) {
        setIsVisible(true);
      } else if (currentScrollY > lastScrollY) {
        setIsVisible(false);
      } else {
        setIsVisible(true);
      }
      
      setLastScrollY(currentScrollY);
    };

    window.addEventListener("scroll", controlNavbar);
    return () => window.removeEventListener("scroll", controlNavbar);
  }, [lastScrollY]);

  const scrollToSection = (id: string) => {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  };

  return (
    <nav className={`fixed top-0 left-0 right-0 z-50 bg-primary/95 backdrop-blur-sm border-b border-primary-foreground/10 transition-transform duration-300 ${
      isVisible ? "translate-y-0" : "-translate-y-full"
    }`}>
      <div className="container mx-auto max-w-6xl px-6">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3">
            <img src={logoBranco} alt="Logo" className="h-10 object-contain" />
          </div>
          
          <div className="flex items-center gap-6">
            <button
              onClick={() => scrollToSection("stock")}
              className="text-primary-foreground hover:text-primary-foreground/80 transition-colors font-medium"
            >
              Stock
            </button>
            <button
              onClick={() => scrollToSection("campanhas")}
              className="text-primary-foreground hover:text-primary-foreground/80 transition-colors font-medium"
            >
              Campanhas
            </button>
            <button
              onClick={() => scrollToSection("doacoes")}
              className="text-primary-foreground hover:text-primary-foreground/80 transition-colors font-medium"
            >
              Doações
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;

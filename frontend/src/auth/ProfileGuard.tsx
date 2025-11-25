import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { getProfile } from "../api/api";
import { Loader2 } from "lucide-react";

interface ProfileGuardProps {
  children: React.ReactNode;
}

const ProfileGuard: React.FC<ProfileGuardProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkProfile = async () => {
      try {
        const user = await getProfile();
        
        // LA LÓGICA DEL DETECTOR:
        // Si altura y peso son 1.0 (tus valores dummy de Java), es un usuario incompleto.
        const isDummyProfile = user.height === 1.0 && user.weight === 1.0;

        // Si es dummy y NO estamos ya en el onboarding, mandar al onboarding
        if (isDummyProfile && location.pathname !== "/onboarding") {
          console.warn("Perfil incompleto detectado. Redirigiendo...");
          // Pasamos el usuario y un flag 'isGoogle' para que el Onboarding sepa qué hacer
          navigate("/onboarding", { state: { googleUser: user, isUpdateMode: true } });
        }
      } catch (error) {
        console.error("Error verificando perfil:", error);
      } finally {
        setIsLoading(false);
      }
    };

    checkProfile();
  }, [navigate, location]);

  if (isLoading) {
    return (
      <div className="h-screen bg-black flex items-center justify-center text-yellow-500">
        <Loader2 className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  return <>{children}</>;
};

export default ProfileGuard;
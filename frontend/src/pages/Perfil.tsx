"use client"

import type React from "react"
import { useEffect, useState } from "react"
import {
  Camera, Mail, Phone, Calendar, Weight, Ruler, Target, Activity, Edit2, Trophy, Award, Users, Sparkles, Check, X
} from "lucide-react"
// IMPORTANTE: Importar la función de actualizar desde la API
import { updateProfile, getProfile } from "../api/api"

// Definición ajustada a tu Backend
interface UserData {
  id?: number // Necesitamos el ID para hacer el PUT
  username: string
  firstName: string
  lastName: string
  gender: string
  age?: number
  birthday?: string
  weight: number
  height: number
  goal?: string
  activityLevel?: string
  role: "LEARNER" | "TRAINER" | "learner" | "trainer"
  email: string
  phoneNumber?: string
}

const Perfil: React.FC = () => {
  const [userData, setUserData] = useState<UserData | null>(null)
  const [isEditing, setIsEditing] = useState(false)
  const [showRoleModal, setShowRoleModal] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  // Cargar datos al iniciar
  useEffect(() => {
    const fetchLatestData = async () => {
        try {
            // Pedir datos frescos al backend siempre al cargar
            const profile = await getProfile();
            setUserData(profile);
            // Sincronizar localStorage
            localStorage.setItem("userData", JSON.stringify(profile));
        } catch (e) {
            console.error("Error cargando perfil:", e);
            // Fallback a lo que haya en localStorage si falla la red
            const storedData = localStorage.getItem("userData")
            if (storedData) setUserData(JSON.parse(storedData))
        }
    };
    fetchLatestData();
  }, [])

  // Calcular edad
  const calculateAge = (birthday?: string) => {
    if (!birthday) return 0;
    const birthDate = new Date(birthday);
    const ageDifMs = Date.now() - birthDate.getTime();
    const ageDate = new Date(ageDifMs);
    return Math.abs(ageDate.getUTCFullYear() - 1970);
  }
  const displayAge = userData?.age || calculateAge(userData?.birthday);

  // Calcular IMC
  const calculateIMC = () => {
    if (!userData || !userData.weight || !userData.height || userData.height <= 0) return "-";
    const heightMeters = userData.height > 3 ? userData.height / 100 : userData.height; 
    const imc = userData.weight / (heightMeters * heightMeters);
    return isNaN(imc) || !isFinite(imc) ? "-" : imc.toFixed(1);
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!userData || !userData.id) return;

    setIsLoading(true);
    try {
        // 1. LIMPIEZA DE DATOS:
        // Creamos un objeto nuevo SOLO con los campos que User.java acepta.
        // Excluimos campos calculados o de otras tablas como 'age', 'goal', 'activityLevel' para que Java no falle.
        const payload = {
            firstName: userData.firstName,
            lastName: userData.lastName,
            username: userData.username,
            phoneNumber: userData.phoneNumber,
            email: userData.email,
            // Si quieres permitir editar estos, asegúrate de que estén en el form
            gender: userData.gender,
            birthday: userData.birthday,
            height: userData.height,
            weight: userData.weight,
            userStatus: "AVAILABLE" // O el que corresponda
        };

        console.log("Enviando payload limpio:", payload);

        // 2. Llamada a la API con el payload limpio
        const updatedUser = await updateProfile(userData.id, payload);
        
        // Actualizar estado y localStorage
        setUserData(updatedUser);
        localStorage.setItem("userData", JSON.stringify(updatedUser));
        
        setIsEditing(false);
        alert("¡Perfil actualizado correctamente!"); 
    } catch (error) {
        console.error("Error actualizando perfil:", error);
        alert("Error al guardar los cambios. Revisa la consola.");
    } finally {
        setIsLoading(false);
    }
  }

  const handleBecomeTrainer = () => {
    if (userData) {
      const updatedData = { ...userData, role: "trainer" as const }
      setUserData(updatedData)
      localStorage.setItem("userData", JSON.stringify(updatedData))
      setShowRoleModal(false)
    }
  }

  // Helpers de etiquetas
  const getGoalLabel = (goal?: string) => {
      if (!goal) return "-";
      const goals: Record<string, string> = { "lose-weight": "Perder peso", "gain-muscle": "Ganar masa muscular", "endurance": "Resistencia", "gain-weight": "Aumentar peso", "health": "Salud general", "other": "Otro" };
      return goals[goal] || goal;
  }
  const getActivityLabel = (level?: string) => {
      if (!level) return "-";
      const levels: Record<string, string> = { beginner: "Principiante", intermediate: "Intermedio", advanced: "Avanzado" };
      return levels[level] || level;
  }

  if (!userData) return <div className="min-h-screen bg-black text-white flex items-center justify-center">Cargando perfil...</div>;

  return (
    <div className="min-h-screen bg-black">
      <div className="max-w-6xl mx-auto px-6 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-yellow-400 to-yellow-600 bg-clip-text text-transparent mb-2">Mi Perfil</h1>
          <p className="text-lg text-gray-400">Gestiona tu información personal y preferencias</p>
        </div>

        {/* Banner Entrenador */}
        {userData.role.toLowerCase() === "learner" && (
          <div className="mb-6 bg-gradient-to-r from-yellow-500 to-yellow-600 rounded-2xl p-6 text-black shadow-lg animate-fadeIn flex justify-between items-center flex-wrap gap-4">
             <div className="flex items-center gap-4">
                <div className="w-12 h-12 bg-black/20 rounded-xl flex items-center justify-center"><Award className="w-6 h-6" /></div>
                <div><h3 className="text-xl font-bold mb-1">Conviértete en Entrenador</h3><p className="text-black/80 text-sm">Comparte tu conocimiento y crea entrenamientos</p></div>
             </div>
             <button onClick={() => setShowRoleModal(true)} className="px-6 py-3 bg-black text-yellow-500 font-semibold rounded-xl hover:bg-zinc-900 transition-all shadow-lg">Ser Entrenador</button>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left Column - Profile Card */}
          <div className="space-y-6">
            <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-8 text-center">
              <div className="relative w-32 h-32 mx-auto mb-6 group">
                <div className="w-full h-full bg-gradient-to-br from-yellow-400 to-yellow-600 rounded-full flex items-center justify-center text-black text-4xl font-bold shadow-lg overflow-hidden">
                   <span>{userData.firstName?.[0]?.toUpperCase()}{userData.lastName?.[0]?.toUpperCase()}</span>
                </div>
                <button className="absolute bottom-0 right-0 w-10 h-10 bg-yellow-500 hover:bg-yellow-600 rounded-full flex items-center justify-center text-black shadow-lg border-2 border-black"><Camera className="w-5 h-5" /></button>
              </div>
              <h2 className="text-2xl font-bold text-white mb-1">{userData.firstName} {userData.lastName}</h2>
              <p className="text-gray-400">@{userData.username}</p>
              
              <div className="mt-6 space-y-3 text-left">
                <div className="flex items-center gap-3 p-3 bg-black/50 rounded-xl border border-zinc-800">
                  <div className="w-10 h-10 bg-yellow-500/20 rounded-lg flex items-center justify-center"><Trophy className="w-5 h-5 text-yellow-500" /></div>
                  <div><p className="text-sm text-gray-400">Nivel</p><p className="font-semibold text-white">{getActivityLabel(userData.activityLevel)}</p></div>
                </div>
                <div className="flex items-center gap-3 p-3 bg-black/50 rounded-xl border border-zinc-800">
                  <div className="w-10 h-10 bg-yellow-500/20 rounded-lg flex items-center justify-center"><Target className="w-5 h-5 text-yellow-500" /></div>
                  <div><p className="text-sm text-gray-400">Objetivo</p><p className="font-semibold text-white">{getGoalLabel(userData.goal)}</p></div>
                </div>
              </div>
            </div>
          </div>

          {/* Right Column - Details Form */}
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-8">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-2xl font-bold text-white">Información Personal</h3>
                {!isEditing ? (
                    <button onClick={() => setIsEditing(true)} className="flex items-center gap-2 px-4 py-2 text-yellow-500 hover:bg-yellow-500/10 rounded-xl border border-yellow-500/50 transition-all">
                        <Edit2 className="w-4 h-4" /> Editar
                    </button>
                ) : (
                    <button onClick={() => setIsEditing(false)} className="flex items-center gap-2 px-4 py-2 text-red-400 hover:bg-red-500/10 rounded-xl border border-red-500/50 transition-all">
                        <X className="w-4 h-4" /> Cancelar
                    </button>
                )}
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Nombre</label>
                    <input
                      type="text"
                      value={userData.firstName}
                      onChange={(e) => setUserData({...userData, firstName: e.target.value})}
                      disabled={!isEditing}
                      className={`w-full px-4 py-3 bg-black/50 border rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-all ${isEditing ? "border-zinc-600" : "border-transparent cursor-not-allowed text-gray-400"}`}
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Apellido</label>
                    <input
                      type="text"
                      value={userData.lastName}
                      onChange={(e) => setUserData({...userData, lastName: e.target.value})}
                      disabled={!isEditing}
                      className={`w-full px-4 py-3 bg-black/50 border rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-all ${isEditing ? "border-zinc-600" : "border-transparent cursor-not-allowed text-gray-400"}`}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Email</label>
                    <div className="relative">
                      <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                      <input
                        type="email"
                        value={userData.email || ""} 
                        disabled // El email no se suele editar fácilmente
                        className="w-full pl-12 pr-4 py-3 bg-black/50 border border-transparent rounded-xl text-gray-500 cursor-not-allowed"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Teléfono</label>
                    <div className="relative">
                      <Phone className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                      <input
                        type="tel"
                        value={userData.phoneNumber || ""}
                        onChange={(e) => setUserData({...userData, phoneNumber: e.target.value})}
                        disabled={!isEditing}
                        placeholder="Agrega tu teléfono"
                        className={`w-full pl-12 pr-4 py-3 bg-black/50 border rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-all ${isEditing ? "border-zinc-600" : "border-transparent cursor-not-allowed text-gray-400"}`}
                      />
                    </div>
                  </div>
                </div>

                {isEditing && (
                  <div className="flex justify-end pt-4 border-t border-zinc-800 animate-in fade-in slide-in-from-top-2">
                    <button
                      type="submit"
                      disabled={isLoading}
                      className="flex items-center gap-2 px-8 py-3 bg-yellow-500 text-black font-bold rounded-xl hover:bg-yellow-600 transition-all shadow-lg shadow-yellow-500/20 disabled:opacity-70"
                    >
                      {isLoading ? "Guardando..." : <><Check className="w-5 h-5" /> Guardar Cambios</>}
                    </button>
                  </div>
                )}
              </form>
            </div>

            {/* Stats (Read Only) */}
            <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-8">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-2xl font-bold text-white">Datos Físicos</h3>
                {/* Botón opcional para ir al onboarding y re-editar medidas */}
                <button onClick={() => alert("Ir a editar medidas (Implementar navegación)")} className="text-sm text-yellow-500 hover:underline">Actualizar medidas</button>
              </div>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                 <div className="p-4 bg-black/50 rounded-xl border border-zinc-800"><p className="text-sm text-gray-400 mb-1">Edad</p><p className="text-2xl font-bold text-white">{displayAge}<span className="text-sm text-gray-500 ml-1">años</span></p></div>
                 <div className="p-4 bg-black/50 rounded-xl border border-zinc-800"><p className="text-sm text-gray-400 mb-1">Peso</p><p className="text-2xl font-bold text-white">{userData.weight}<span className="text-sm text-gray-500 ml-1">kg</span></p></div>
                 <div className="p-4 bg-black/50 rounded-xl border border-zinc-800"><p className="text-sm text-gray-400 mb-1">Altura</p><p className="text-2xl font-bold text-white">{userData.height > 3 ? userData.height : userData.height * 100}<span className="text-sm text-gray-500 ml-1">cm</span></p></div>
                 <div className="p-4 bg-black/50 rounded-xl border border-zinc-800"><p className="text-sm text-gray-400 mb-1">IMC</p><p className="text-2xl font-bold text-white">{calculateIMC()}</p></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      {/* Role Modal (mismo código de antes) */}
      {showRoleModal && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"><div className="bg-zinc-900 border border-zinc-800 rounded-2xl p-8 max-w-md text-center"><h2 className="text-2xl font-bold text-white mb-4">Próximamente</h2><button onClick={() => setShowRoleModal(false)} className="px-6 py-2 bg-zinc-800 text-white rounded-lg">Cerrar</button></div></div>
      )}
    </div>
  )
}

export default Perfil
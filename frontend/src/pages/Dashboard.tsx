"use client"

import type React from "react"
import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import {
  Dumbbell,
  Target,
  TrendingUp,
  Users,
  Calendar,
  Trophy,
  BookOpen,
  Apple,
  Zap,
  Award,
  Activity,
  Heart,
} from "lucide-react"

interface UserData {
  username: string
  firstName: string
  lastName: string
  goal: string
  activityLevel: string
}

const Dashboard: React.FC = () => {
  const [userData, setUserData] = useState<UserData | null>(null)
  const [stats, setStats] = useState({
    completedWorkouts: 0,
    weeklyProgress: 0,
    goalsAchieved: 0,
    totalGoals: 5,
    activeDays: 0,
  })

  useEffect(() => {
    const storedData = localStorage.getItem("userData")
    if (storedData) {
      setUserData(JSON.parse(storedData))
    }

    const storedStats = localStorage.getItem("userStats")
    if (storedStats) {
      setStats(JSON.parse(storedStats))
    } else {
      const defaultStats = {
        completedWorkouts: 12,
        weeklyProgress: 85,
        goalsAchieved: 3,
        totalGoals: 5,
        activeDays: 24,
      }
      setStats(defaultStats)
      localStorage.setItem("userStats", JSON.stringify(defaultStats))
    }
  }, [])

  const getGreeting = () => {
    const hour = new Date().getHours()
    if (hour < 12) return "Buenos días"
    if (hour < 18) return "Buenas tardes"
    return "Buenas noches"
  }

  return (
    <div className="min-h-screen bg-black">
      <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/5 via-transparent to-yellow-600/5 pointer-events-none"></div>

      <div className="relative max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
          <h1 className="text-5xl font-bold bg-gradient-to-r from-yellow-400 via-yellow-500 to-yellow-600 bg-clip-text text-transparent mb-3">
            {getGreeting()}, {userData?.firstName || "Usuario"}!
          </h1>
          <p className="text-xl text-zinc-400">Listo para superar tus límites hoy?</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10 animate-in fade-in slide-in-from-bottom-8 duration-700 delay-150">
          <div className="group relative bg-gradient-to-br from-yellow-500 to-yellow-600 rounded-2xl p-6 shadow-2xl shadow-yellow-500/20 hover:shadow-yellow-500/40 transition-all duration-300 hover:scale-105">
            <div className="absolute inset-0 bg-gradient-to-br from-white/10 to-transparent rounded-2xl"></div>
            <div className="relative">
              <div className="flex items-center justify-between mb-4">
                <div className="w-14 h-14 bg-black/20 backdrop-blur-xl rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <Trophy className="w-7 h-7 text-black" />
                </div>
                <span className="text-4xl font-black text-black">{stats.completedWorkouts}</span>
              </div>
              <p className="text-black font-semibold text-sm uppercase tracking-wide">Entrenamientos</p>
              <p className="text-black/70 text-xs mt-1">Este mes</p>
            </div>
          </div>

          <div className="group relative bg-zinc-900 border border-zinc-800 rounded-2xl p-6 shadow-xl hover:shadow-2xl hover:border-yellow-500/50 transition-all duration-300 hover:scale-105">
            <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            <div className="relative">
              <div className="flex items-center justify-between mb-4">
                <div className="w-14 h-14 bg-yellow-500/10 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <TrendingUp className="w-7 h-7 text-yellow-500" />
                </div>
                <span className="text-4xl font-black text-white">{stats.weeklyProgress}%</span>
              </div>
              <p className="text-white font-semibold text-sm uppercase tracking-wide">Progreso</p>
              <p className="text-zinc-400 text-xs mt-1">Esta semana</p>
            </div>
          </div>

          <div className="group relative bg-zinc-900 border border-zinc-800 rounded-2xl p-6 shadow-xl hover:shadow-2xl hover:border-yellow-500/50 transition-all duration-300 hover:scale-105">
            <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            <div className="relative">
              <div className="flex items-center justify-between mb-4">
                <div className="w-14 h-14 bg-yellow-500/10 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <Target className="w-7 h-7 text-yellow-500" />
                </div>
                <span className="text-4xl font-black text-white">
                  {stats.goalsAchieved}/{stats.totalGoals}
                </span>
              </div>
              <p className="text-white font-semibold text-sm uppercase tracking-wide">Metas</p>
              <p className="text-zinc-400 text-xs mt-1">Alcanzadas</p>
            </div>
          </div>

          <div className="group relative bg-zinc-900 border border-zinc-800 rounded-2xl p-6 shadow-xl hover:shadow-2xl hover:border-yellow-500/50 transition-all duration-300 hover:scale-105">
            <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            <div className="relative">
              <div className="flex items-center justify-between mb-4">
                <div className="w-14 h-14 bg-yellow-500/10 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                  <Calendar className="w-7 h-7 text-yellow-500" />
                </div>
                <span className="text-4xl font-black text-white">{stats.activeDays}</span>
              </div>
              <p className="text-white font-semibold text-sm uppercase tracking-wide">Racha</p>
              <p className="text-zinc-400 text-xs mt-1">Días activos</p>
            </div>
          </div>
        </div>

        {/* Main Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Column */}
          <div className="lg:col-span-2 space-y-8">
            <div className="bg-zinc-900 border border-zinc-800 rounded-3xl shadow-2xl p-8 animate-in fade-in slide-in-from-left duration-700 delay-300">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-10 h-10 bg-yellow-500 rounded-xl flex items-center justify-center">
                  <Zap className="w-5 h-5 text-black" />
                </div>
                <h2 className="text-3xl font-bold text-white">Acceso rápido</h2>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                <Link
                  to="/dashboard/entrenamiento"
                  className="group relative p-7 bg-gradient-to-br from-yellow-500/10 to-yellow-600/5 rounded-2xl border-2 border-yellow-500/20 hover:border-yellow-500 transition-all duration-300 hover:shadow-xl hover:shadow-yellow-500/20 overflow-hidden"
                >
                  <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/0 to-yellow-500/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                  <div className="relative">
                    <div className="w-14 h-14 bg-yellow-500 rounded-2xl flex items-center justify-center mb-5 group-hover:scale-110 group-hover:rotate-3 transition-all duration-300 shadow-lg shadow-yellow-500/50">
                      <Dumbbell className="w-7 h-7 text-black" />
                    </div>
                    <h3 className="text-xl font-bold text-white mb-2">Entrenamientos</h3>
                    <p className="text-sm text-zinc-400">Rutinas personalizadas y seguimiento</p>
                  </div>
                </Link>

                <Link
                  to="/dashboard/nutricion"
                  className="group relative p-7 bg-gradient-to-br from-yellow-500/10 to-yellow-600/5 rounded-2xl border-2 border-yellow-500/20 hover:border-yellow-500 transition-all duration-300 hover:shadow-xl hover:shadow-yellow-500/20 overflow-hidden"
                >
                  <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/0 to-yellow-500/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                  <div className="relative">
                    <div className="w-14 h-14 bg-yellow-500 rounded-2xl flex items-center justify-center mb-5 group-hover:scale-110 group-hover:rotate-3 transition-all duration-300 shadow-lg shadow-yellow-500/50">
                      <Apple className="w-7 h-7 text-black" />
                    </div>
                    <h3 className="text-xl font-bold text-white mb-2">Nutrición</h3>
                    <p className="text-sm text-zinc-400">Planes alimenticios y recetas</p>
                  </div>
                </Link>

                <Link
                  to="/dashboard/comunidad"
                  className="group relative p-7 bg-gradient-to-br from-yellow-500/10 to-yellow-600/5 rounded-2xl border-2 border-yellow-500/20 hover:border-yellow-500 transition-all duration-300 hover:shadow-xl hover:shadow-yellow-500/20 overflow-hidden"
                >
                  <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/0 to-yellow-500/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                  <div className="relative">
                    <div className="w-14 h-14 bg-yellow-500 rounded-2xl flex items-center justify-center mb-5 group-hover:scale-110 group-hover:rotate-3 transition-all duration-300 shadow-lg shadow-yellow-500/50">
                      <Users className="w-7 h-7 text-black" />
                    </div>
                    <h3 className="text-xl font-bold text-white mb-2">Comunidad</h3>
                    <p className="text-sm text-zinc-400">Conecta con otros atletas</p>
                  </div>
                </Link>

                <Link
                  to="/dashboard/articulos"
                  className="group relative p-7 bg-gradient-to-br from-yellow-500/10 to-yellow-600/5 rounded-2xl border-2 border-yellow-500/20 hover:border-yellow-500 transition-all duration-300 hover:shadow-xl hover:shadow-yellow-500/20 overflow-hidden"
                >
                  <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/0 to-yellow-500/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                  <div className="relative">
                    <div className="w-14 h-14 bg-yellow-500 rounded-2xl flex items-center justify-center mb-5 group-hover:scale-110 group-hover:rotate-3 transition-all duration-300 shadow-lg shadow-yellow-500/50">
                      <BookOpen className="w-7 h-7 text-black" />
                    </div>
                    <h3 className="text-xl font-bold text-white mb-2">Artículos</h3>
                    <p className="text-sm text-zinc-400">Tips y consejos expertos</p>
                  </div>
                </Link>
              </div>
            </div>

            <div className="relative bg-gradient-to-br from-zinc-900 via-zinc-900 to-zinc-950 rounded-3xl shadow-2xl p-10 border border-zinc-800 overflow-hidden animate-in fade-in slide-in-from-left duration-700 delay-500">
              <div className="absolute top-0 right-0 w-96 h-96 bg-yellow-500/10 rounded-full blur-3xl"></div>
              <div className="absolute bottom-0 left-0 w-64 h-64 bg-yellow-600/5 rounded-full blur-3xl"></div>
              <div className="relative z-10">
                <div className="flex items-start justify-between mb-6">
                  <div className="flex items-center gap-4">
                    <div className="w-16 h-16 bg-gradient-to-br from-yellow-500 to-yellow-600 rounded-2xl flex items-center justify-center shadow-xl shadow-yellow-500/50">
                      <Trophy className="w-8 h-8 text-black" />
                    </div>
                    <div>
                      <p className="text-sm text-yellow-500 font-semibold uppercase tracking-wider mb-1">
                        Reto de la semana
                      </p>
                      <h3 className="text-3xl font-black text-white">100 Flexiones Challenge</h3>
                    </div>
                  </div>
                  <div className="px-4 py-2 bg-yellow-500/20 rounded-full border border-yellow-500/50">
                    <span className="text-yellow-500 font-bold text-sm">4 días restantes</span>
                  </div>
                </div>
                <p className="text-zinc-300 mb-8 text-lg leading-relaxed">
                  Completa 100 flexiones durante la semana. Puedes dividirlas como quieras y desafiar a tus amigos!
                </p>
                <div className="mb-8">
                  <div className="flex justify-between text-sm mb-3">
                    <span className="text-zinc-400 font-medium">Tu progreso</span>
                    <span className="text-yellow-500 font-bold text-lg">45/100 flexiones</span>
                  </div>
                  <div className="relative w-full h-4 bg-zinc-800 rounded-full overflow-hidden">
                    <div className="absolute inset-0 bg-gradient-to-r from-zinc-800 to-zinc-700"></div>
                    <div
                      className="relative h-full bg-gradient-to-r from-yellow-500 to-yellow-600 rounded-full shadow-lg shadow-yellow-500/50 transition-all duration-700"
                      style={{ width: "45%" }}
                    >
                      <div className="absolute inset-0 bg-gradient-to-r from-white/20 to-transparent"></div>
                    </div>
                  </div>
                </div>
                <div className="flex gap-4">
                  <Link
                    to="/dashboard/reto-semanal"
                    className="flex-1 inline-flex items-center justify-center gap-3 bg-gradient-to-r from-yellow-500 to-yellow-600 hover:from-yellow-600 hover:to-yellow-700 text-black px-8 py-4 rounded-2xl font-bold text-lg transition-all duration-300 shadow-xl shadow-yellow-500/30 hover:shadow-yellow-500/50 hover:scale-105"
                  >
                    <Award className="w-5 h-5" />
                    Ver detalles del reto
                  </Link>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            {/* Sesiones Recomendadas */}
            <div className="bg-zinc-900 border border-zinc-800 rounded-3xl shadow-2xl p-7 animate-in fade-in slide-in-from-right duration-700 delay-300">
              <div className="flex items-center gap-3 mb-6">
                <div className="w-10 h-10 bg-yellow-500 rounded-xl flex items-center justify-center">
                  <Activity className="w-5 h-5 text-black" />
                </div>
                <h2 className="text-2xl font-bold text-white">Recomendadas</h2>
              </div>
              <div className="space-y-4">
                <div className="group p-5 bg-gradient-to-br from-yellow-500/10 to-yellow-600/5 rounded-2xl border border-yellow-500/20 hover:border-yellow-500 transition-all duration-300 hover:shadow-lg hover:shadow-yellow-500/20">
                  <div className="flex items-start justify-between mb-3">
                    <h4 className="font-bold text-white text-lg">Cardio Intenso</h4>
                    <div className="px-3 py-1 bg-yellow-500 rounded-full">
                      <span className="text-black text-xs font-bold">NUEVO</span>
                    </div>
                  </div>
                  <div className="flex items-center gap-3 mb-4 text-zinc-400 text-sm">
                    <span className="flex items-center gap-1">
                      <Clock className="w-4 h-4" />
                      30 min
                    </span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <Zap className="w-4 h-4" />
                      Alta intensidad
                    </span>
                  </div>
                  <button className="w-full py-3 bg-yellow-500 hover:bg-yellow-600 text-black rounded-xl font-bold transition-colors duration-300 group-hover:scale-105 transform">
                    Iniciar sesión
                  </button>
                </div>

                <div className="group p-5 bg-zinc-800/50 rounded-2xl border border-zinc-700 hover:border-yellow-500/50 transition-all duration-300">
                  <h4 className="font-bold text-white text-lg mb-3">Fuerza Upper Body</h4>
                  <div className="flex items-center gap-3 mb-4 text-zinc-400 text-sm">
                    <span>45 min</span>
                    <span>•</span>
                    <span>Media intensidad</span>
                  </div>
                  <button className="w-full py-3 bg-zinc-700 hover:bg-yellow-500 hover:text-black text-white rounded-xl font-bold transition-all duration-300">
                    Iniciar sesión
                  </button>
                </div>

                <div className="group p-5 bg-zinc-800/50 rounded-2xl border border-zinc-700 hover:border-yellow-500/50 transition-all duration-300">
                  <h4 className="font-bold text-white text-lg mb-3">Yoga & Flexibilidad</h4>
                  <div className="flex items-center gap-3 mb-4 text-zinc-400 text-sm">
                    <span>20 min</span>
                    <span>•</span>
                    <span>Baja intensidad</span>
                  </div>
                  <button className="w-full py-3 bg-zinc-700 hover:bg-yellow-500 hover:text-black text-white rounded-xl font-bold transition-all duration-300">
                    Iniciar sesión
                  </button>
                </div>
              </div>
              <Link
                to="/dashboard/sesiones"
                className="block mt-6 text-center text-sm font-bold text-yellow-500 hover:text-yellow-400 transition-colors"
              >
                Ver todas las sesiones →
              </Link>
            </div>

            <div className="bg-zinc-900 border border-zinc-800 rounded-3xl shadow-2xl p-7 animate-in fade-in slide-in-from-right duration-700 delay-500">
              <div className="flex items-center gap-3 mb-6">
                <div className="w-10 h-10 bg-yellow-500 rounded-xl flex items-center justify-center">
                  <Heart className="w-5 h-5 text-black" />
                </div>
                <h2 className="text-2xl font-bold text-white">Actividad</h2>
              </div>
              <div className="space-y-5">
                <div className="flex items-start gap-4 group">
                  <div className="relative flex-shrink-0">
                    <div className="w-3 h-3 bg-yellow-500 rounded-full mt-2 ring-4 ring-yellow-500/20"></div>
                    <div className="absolute top-5 left-1.5 w-0.5 h-full bg-zinc-800"></div>
                  </div>
                  <div className="flex-1 pt-1">
                    <p className="text-sm text-white font-semibold mb-1">Completaste "Cardio HIIT"</p>
                    <p className="text-xs text-zinc-500">Hace 2 horas • 400 calorías quemadas</p>
                  </div>
                </div>
                <div className="flex items-start gap-4 group">
                  <div className="relative flex-shrink-0">
                    <div className="w-3 h-3 bg-yellow-500 rounded-full mt-2 ring-4 ring-yellow-500/20"></div>
                    <div className="absolute top-5 left-1.5 w-0.5 h-full bg-zinc-800"></div>
                  </div>
                  <div className="flex-1 pt-1">
                    <p className="text-sm text-white font-semibold mb-1">Nueva meta alcanzada 🎯</p>
                    <p className="text-xs text-zinc-500">Ayer • 10 entrenamientos completados</p>
                  </div>
                </div>
                <div className="flex items-start gap-4 group">
                  <div className="relative flex-shrink-0">
                    <div className="w-3 h-3 bg-yellow-500 rounded-full mt-2 ring-4 ring-yellow-500/20"></div>
                  </div>
                  <div className="flex-1 pt-1">
                    <p className="text-sm text-white font-semibold mb-1">Te uniste a la comunidad</p>
                    <p className="text-xs text-zinc-500">Hace 3 días</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

import { Clock } from "lucide-react"

export default Dashboard

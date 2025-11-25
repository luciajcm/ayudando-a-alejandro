"use client"

import { useState } from "react"
import { Dumbbell, Clock, Flame, TrendingUp, Play, Check, ChevronRight } from "lucide-react"

function Sesiones() {
  const [selectedLevel, setSelectedLevel] = useState("all")

  const sessions = [
    {
      id: 1,
      title: "HIIT Total Body",
      duration: 30,
      difficulty: "Intermedio",
      calories: 350,
      exercises: 8,
      image: "🔥",
      color: "from-yellow-500 to-orange-500",
      category: "Cardio",
      completed: false,
    },
    {
      id: 2,
      title: "Fuerza Upper Body",
      duration: 45,
      difficulty: "Avanzado",
      calories: 280,
      exercises: 10,
      image: "💪",
      color: "from-yellow-400 to-yellow-600",
      category: "Fuerza",
      completed: true,
    },
    {
      id: 3,
      title: "Yoga Flow Matutino",
      duration: 25,
      difficulty: "Principiante",
      calories: 120,
      exercises: 12,
      image: "🧘",
      color: "from-zinc-700 to-zinc-900",
      category: "Flexibilidad",
      completed: false,
    },
    {
      id: 4,
      title: "Core y Abdominales",
      duration: 20,
      difficulty: "Intermedio",
      calories: 180,
      exercises: 6,
      image: "💥",
      color: "from-yellow-500 to-yellow-700",
      category: "Core",
      completed: false,
    },
    {
      id: 5,
      title: "Piernas y Glúteos",
      duration: 40,
      difficulty: "Intermedio",
      calories: 320,
      exercises: 9,
      image: "🦵",
      color: "from-yellow-600 to-orange-600",
      category: "Fuerza",
      completed: false,
    },
    {
      id: 6,
      title: "Cardio + Resistencia",
      duration: 35,
      difficulty: "Avanzado",
      calories: 400,
      exercises: 7,
      image: "⚡",
      color: "from-yellow-400 to-yellow-600",
      category: "Cardio",
      completed: false,
    },
  ]

  const quickStats = [
    { label: "Sesiones Completadas", value: "24", icon: Check, color: "text-green-400" },
    { label: "Total Minutos", value: "680", icon: Clock, color: "text-blue-400" },
    { label: "Calorías Quemadas", value: "5,420", icon: Flame, color: "text-orange-400" },
    { label: "Racha Actual", value: "7 días", icon: TrendingUp, color: "text-purple-400" },
  ]

  const difficulties = ["all", "Principiante", "Intermedio", "Avanzado"]

  const filteredSessions = selectedLevel === "all" ? sessions : sessions.filter((s) => s.difficulty === selectedLevel)

  return (
    <div className="min-h-screen bg-black text-white">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2 bg-gradient-to-r from-yellow-400 to-yellow-600 bg-clip-text text-transparent">
            Sesiones Recomendadas
          </h1>
          <p className="text-gray-400">Entrenamientos diseñados para alcanzar tus objetivos</p>
        </div>

        {/* Quick Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {quickStats.map((stat) => {
            const Icon = stat.icon
            return (
              <div key={stat.label} className="bg-zinc-900 backdrop-blur-sm rounded-xl p-4 border border-zinc-800">
                <Icon className={`w-6 h-6 mb-2 ${stat.color}`} />
                <p className="text-2xl font-bold mb-1">{stat.value}</p>
                <p className="text-xs text-gray-400">{stat.label}</p>
              </div>
            )
          })}
        </div>

        {/* Filtros */}
        <div className="flex gap-3 mb-6 overflow-x-auto pb-2">
          {difficulties.map((level) => (
            <button
              key={level}
              onClick={() => setSelectedLevel(level)}
              className={`px-4 py-2 rounded-lg whitespace-nowrap transition-all ${
                selectedLevel === level
                  ? "bg-yellow-500 text-black font-semibold"
                  : "bg-zinc-900 text-gray-400 hover:bg-zinc-800"
              }`}
            >
              {level === "all" ? "Todos" : level}
            </button>
          ))}
        </div>

        {/* Sessions Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredSessions.map((session) => (
            <div
              key={session.id}
              className="bg-zinc-900 backdrop-blur-sm rounded-xl overflow-hidden border border-zinc-800 hover:border-yellow-500/50 transition-all hover:scale-105 group cursor-pointer"
            >
              {/* Imagen */}
              <div
                className={`bg-gradient-to-br ${session.color} h-40 flex items-center justify-center text-6xl relative`}
              >
                {session.image}
                {session.completed && (
                  <div className="absolute top-3 right-3 bg-yellow-500 rounded-full p-2">
                    <Check className="w-4 h-4 text-black" />
                  </div>
                )}
              </div>

              {/* Content */}
              <div className="p-5">
                <div className="flex items-center justify-between mb-3">
                  <span className="bg-zinc-800 text-gray-300 text-xs px-2 py-1 rounded-full">{session.category}</span>
                  <span
                    className={`text-xs font-semibold ${
                      session.difficulty === "Principiante"
                        ? "text-yellow-400"
                        : session.difficulty === "Intermedio"
                          ? "text-yellow-500"
                          : "text-orange-400"
                    }`}
                  >
                    {session.difficulty}
                  </span>
                </div>

                <h3 className="text-xl font-bold mb-3 group-hover:text-yellow-400 transition-colors">
                  {session.title}
                </h3>

                <div className="grid grid-cols-3 gap-3 mb-4 text-sm text-gray-400">
                  <div className="flex flex-col items-center">
                    <Clock className="w-4 h-4 mb-1" />
                    <span>{session.duration} min</span>
                  </div>
                  <div className="flex flex-col items-center">
                    <Flame className="w-4 h-4 mb-1" />
                    <span>{session.calories} kcal</span>
                  </div>
                  <div className="flex flex-col items-center">
                    <Dumbbell className="w-4 h-4 mb-1" />
                    <span>{session.exercises} ejerc.</span>
                  </div>
                </div>

                <button className="w-full bg-yellow-500 hover:bg-yellow-400 text-black py-3 rounded-lg font-semibold transition-colors flex items-center justify-center gap-2 group-hover:gap-3">
                  <Play className="w-4 h-4" />
                  {session.completed ? "Repetir" : "Comenzar"}
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Sección de Desafío */}
        <div className="mt-8 bg-gradient-to-r from-yellow-500/10 to-yellow-600/10 rounded-2xl p-8 border border-yellow-500/20">
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div className="flex items-center gap-4">
              <div className="bg-yellow-500/20 p-4 rounded-xl">
                <TrendingUp className="w-8 h-8 text-yellow-400" />
              </div>
              <div>
                <h3 className="text-2xl font-bold mb-1">Desafío del Mes</h3>
                <p className="text-gray-400">Completa 20 sesiones y gana una insignia especial</p>
              </div>
            </div>
            <div className="flex items-center gap-6">
              <div>
                <p className="text-3xl font-bold text-yellow-400">12/20</p>
                <p className="text-sm text-gray-400">completadas</p>
              </div>
              <button className="bg-yellow-500 hover:bg-yellow-400 text-black px-6 py-3 rounded-lg font-semibold transition-colors flex items-center gap-2">
                Ver Progreso
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Sesiones

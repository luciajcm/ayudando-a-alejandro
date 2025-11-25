"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Dumbbell, Clock, TrendingUp, Play, Plus, BookOpen } from "lucide-react"
import { Link } from "react-router-dom"

const Entrenamiento: React.FC = () => {
  const [selectedFilter, setSelectedFilter] = useState("all")
  const [userRole, setUserRole] = useState<"learner" | "trainer">("learner")

  useEffect(() => {
    const storedData = localStorage.getItem("userData")
    if (storedData) {
      const userData = JSON.parse(storedData)
      setUserRole(userData.role || "learner")
    }
  }, [])

  const workouts = [
    {
      id: 1,
      title: "Full Body Workout",
      duration: 45,
      difficulty: "Intermedio",
      category: "fuerza",
      exercises: 12,
      calories: 350,
      image: "🏋️",
      color: "from-blue-500 to-blue-600",
    },
    {
      id: 2,
      title: "Cardio HIIT",
      duration: 30,
      difficulty: "Avanzado",
      category: "cardio",
      exercises: 8,
      calories: 400,
      image: "🔥",
      color: "from-red-500 to-red-600",
    },
    {
      id: 3,
      title: "Upper Body Strength",
      duration: 40,
      difficulty: "Intermedio",
      category: "fuerza",
      exercises: 10,
      calories: 300,
      image: "💪",
      color: "from-green-500 to-green-600",
    },
    {
      id: 4,
      title: "Yoga Flow",
      duration: 25,
      difficulty: "Principiante",
      category: "flexibilidad",
      exercises: 15,
      calories: 150,
      image: "🧘",
      color: "from-purple-500 to-purple-600",
    },
    {
      id: 5,
      title: "Lower Body Power",
      duration: 35,
      difficulty: "Intermedio",
      category: "fuerza",
      exercises: 9,
      calories: 320,
      image: "🦵",
      color: "from-orange-500 to-orange-600",
    },
    {
      id: 6,
      title: "Core Blast",
      duration: 20,
      difficulty: "Principiante",
      category: "core",
      exercises: 8,
      calories: 180,
      image: "⚡",
      color: "from-yellow-500 to-yellow-600",
    },
  ]

  const filters = [
    { id: "all", label: "Todos" },
    { id: "fuerza", label: "Fuerza" },
    { id: "cardio", label: "Cardio" },
    { id: "flexibilidad", label: "Flexibilidad" },
    { id: "core", label: "Core" },
  ]

  const filteredWorkouts = selectedFilter === "all" ? workouts : workouts.filter((w) => w.category === selectedFilter)

  return (
    <div className="min-h-screen bg-black">
      <div className="max-w-7xl mx-auto px-6 py-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-4xl font-bold bg-gradient-to-r from-yellow-400 to-yellow-600 bg-clip-text text-transparent mb-2">
              Entrenamientos
            </h1>
            <p className="text-lg text-gray-400">Explora y comienza tus rutinas de ejercicio</p>
          </div>
          <div className="flex items-center gap-3">
            <Link
              to="/dashboard/ejercicios"
              className="flex items-center gap-2 px-6 py-3 bg-zinc-800 text-white font-semibold rounded-xl hover:bg-zinc-700 transition-all border border-zinc-700"
            >
              <BookOpen className="w-5 h-5" />
              Ejercicios
            </Link>
            <Link
              to="/dashboard/seguimiento"
              className="flex items-center gap-2 px-6 py-3 bg-yellow-500 text-black font-semibold rounded-xl hover:bg-yellow-600 transition-all shadow-lg shadow-yellow-500/25"
            >
              <TrendingUp className="w-5 h-5" />
              Seguimiento
            </Link>
          </div>
        </div>

        {userRole === "trainer" && (
          <div className="mb-8 bg-gradient-to-r from-yellow-500 to-yellow-600 rounded-2xl p-6 text-black shadow-lg shadow-yellow-500/20 animate-fadeIn">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 bg-black/20 rounded-xl flex items-center justify-center">
                  <Dumbbell className="w-6 h-6" />
                </div>
                <div>
                  <h3 className="text-xl font-bold mb-1">Modo Entrenador</h3>
                  <p className="text-black/80">Crea y publica entrenamientos personalizados para la comunidad</p>
                </div>
              </div>
              <Link
                to="/dashboard/crear-entrenamiento"
                className="flex items-center gap-2 px-6 py-3 bg-black text-yellow-500 font-semibold rounded-xl hover:bg-zinc-900 transition-all whitespace-nowrap shadow-lg"
              >
                <Plus className="w-5 h-5" />
                Crear Entrenamiento
              </Link>
            </div>
          </div>
        )}

        {/* Filters */}
        <div className="flex items-center gap-3 mb-8 overflow-x-auto pb-2">
          {filters.map((filter) => (
            <button
              key={filter.id}
              onClick={() => setSelectedFilter(filter.id)}
              className={`px-6 py-3 rounded-xl font-semibold whitespace-nowrap transition-all ${selectedFilter === filter.id
                ? "bg-yellow-500 text-black shadow-lg shadow-yellow-500/25 scale-105"
                : "bg-zinc-900 text-gray-300 border border-zinc-800 hover:border-yellow-500/50"
                }`}
            >
              {filter.label}
            </button>
          ))}
        </div>

        {/* Workouts Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredWorkouts.map((workout, index) => (
            <div
              key={workout.id}
              className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 overflow-hidden group hover:shadow-2xl hover:shadow-yellow-500/10 transition-all duration-300 hover:scale-105 animate-fadeIn"
              style={{ animationDelay: `${index * 100}ms` }}
            >
              {/* Image/Icon Section */}
              <div
                className={`h-40 bg-gradient-to-br ${workout.color} flex items-center justify-center text-7xl relative overflow-hidden`}
              >
                <div className="absolute inset-0 bg-black/20 group-hover:bg-black/0 transition-colors"></div>
                <span className="relative z-10 group-hover:scale-110 transition-transform">{workout.image}</span>
              </div>

              {/* Content */}
              <div className="p-6">
                <div className="flex items-center gap-2 mb-3">
                  <span className="px-3 py-1 bg-yellow-500/20 text-yellow-500 rounded-full text-xs font-semibold border border-yellow-500/50">
                    {workout.difficulty}
                  </span>
                  <span className="px-3 py-1 bg-zinc-800 text-gray-300 rounded-full text-xs font-semibold">
                    {workout.category}
                  </span>
                </div>

                <h3 className="text-xl font-bold text-white mb-3">{workout.title}</h3>

                <div className="grid grid-cols-3 gap-3 mb-4">
                  <div className="text-center">
                    <div className="flex items-center justify-center gap-1 text-gray-500 mb-1">
                      <Clock className="w-4 h-4" />
                    </div>
                    <p className="text-sm font-semibold text-white">{workout.duration} min</p>
                  </div>
                  <div className="text-center">
                    <div className="flex items-center justify-center gap-1 text-gray-500 mb-1">
                      <Dumbbell className="w-4 h-4" />
                    </div>
                    <p className="text-sm font-semibold text-white">{workout.exercises} ejercicios</p>
                  </div>
                  <div className="text-center">
                    <div className="flex items-center justify-center gap-1 text-gray-500 mb-1">
                      <TrendingUp className="w-4 h-4" />
                    </div>
                    <p className="text-sm font-semibold text-white">{workout.calories} cal</p>
                  </div>
                </div>

                <button className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-yellow-500 text-black font-semibold rounded-xl hover:bg-yellow-600 transition-all group shadow-lg shadow-yellow-500/25">
                  <Play className="w-5 h-5 group-hover:scale-110 transition-transform" />
                  Comenzar
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Create Custom Workout - Only for non-trainers */}
        {userRole === "learner" && (
          <div className="mt-12 bg-gradient-to-br from-zinc-900 to-black rounded-2xl p-8 text-white border border-zinc-800 shadow-xl hover:shadow-2xl hover:shadow-yellow-500/10 transition-all">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-2xl font-bold mb-2">Crea tu propio entrenamiento</h2>
                <p className="text-gray-400">Personaliza ejercicios según tus necesidades</p>
              </div>
              <button className="flex items-center gap-2 px-6 py-3 bg-yellow-500 hover:bg-yellow-600 text-black rounded-xl font-semibold transition-all shadow-lg shadow-yellow-500/25 hover:scale-105">
                <Plus className="w-5 h-5" />
                Crear Rutina
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default Entrenamiento

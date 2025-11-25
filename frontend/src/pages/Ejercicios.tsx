"use client"

import type React from "react"
import { useState } from "react"
import { Search, Filter, Dumbbell, Clock, TrendingUp, Play, Info } from "lucide-react"

interface Exercise {
  id: number
  name: string
  muscle: string
  difficulty: string
  equipment: string
  category: string
  modelPath: string
  description: string
  reps: string
  sets: string
  restTime: string
}

const Ejercicios: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedCategory, setSelectedCategory] = useState("all")
  const [selectedExercise, setSelectedExercise] = useState<Exercise | null>(null)

  const exercises: Exercise[] = [
    {
      id: 1,
      name: "Press de Banca",
      muscle: "Pecho",
      difficulty: "Intermedio",
      equipment: "Barra",
      category: "fuerza",
      modelPath: "/models/bench-press.glb",
      description: "Ejercicio fundamental para desarrollar el pecho, hombros y tríceps",
      reps: "8-12",
      sets: "3-4",
      restTime: "90s",
    },
    {
      id: 2,
      name: "Sentadilla",
      muscle: "Piernas",
      difficulty: "Intermedio",
      equipment: "Barra",
      category: "fuerza",
      modelPath: "/models/squat.glb",
      description: "Ejercicio compuesto para piernas y glúteos",
      reps: "10-15",
      sets: "4",
      restTime: "2min",
    },
    {
      id: 3,
      name: "Peso Muerto",
      muscle: "Espalda",
      difficulty: "Avanzado",
      equipment: "Barra",
      category: "fuerza",
      modelPath: "/models/deadlift.glb",
      description: "Ejercicio fundamental para espalda baja, glúteos y cadena posterior",
      reps: "6-10",
      sets: "3-4",
      restTime: "3min",
    },
    {
      id: 4,
      name: "Dominadas",
      muscle: "Espalda",
      difficulty: "Avanzado",
      equipment: "Barra",
      category: "fuerza",
      modelPath: "/models/pullup.glb",
      description: "Ejercicio de peso corporal para espalda y bíceps",
      reps: "6-12",
      sets: "3-4",
      restTime: "2min",
    },
    {
      id: 5,
      name: "Flexiones",
      muscle: "Pecho",
      difficulty: "Principiante",
      equipment: "Sin equipo",
      category: "fuerza",
      modelPath: "/models/pushup.glb",
      description: "Ejercicio básico de peso corporal para pecho, hombros y tríceps",
      reps: "12-20",
      sets: "3",
      restTime: "60s",
    },
    {
      id: 6,
      name: "Plancha",
      muscle: "Core",
      difficulty: "Principiante",
      equipment: "Sin equipo",
      category: "core",
      modelPath: "/models/plank.glb",
      description: "Ejercicio isométrico para fortalecer el core",
      reps: "30-60s",
      sets: "3",
      restTime: "60s",
    },
    {
      id: 7,
      name: "Burpees",
      muscle: "Cuerpo completo",
      difficulty: "Intermedio",
      equipment: "Sin equipo",
      category: "cardio",
      modelPath: "/models/burpee.glb",
      description: "Ejercicio de alta intensidad para todo el cuerpo",
      reps: "10-15",
      sets: "3-4",
      restTime: "90s",
    },
    {
      id: 8,
      name: "Zancadas",
      muscle: "Piernas",
      difficulty: "Principiante",
      equipment: "Mancuernas",
      category: "fuerza",
      modelPath: "/models/lunge.glb",
      description: "Ejercicio unilateral para piernas y glúteos",
      reps: "12-15",
      sets: "3",
      restTime: "60s",
    },
    {
      id: 9,
      name: "Curl de Bíceps",
      muscle: "Bíceps",
      difficulty: "Principiante",
      equipment: "Mancuernas",
      category: "fuerza",
      modelPath: "/models/bicep-curl.glb",
      description: "Ejercicio de aislamiento para bíceps",
      reps: "10-15",
      sets: "3",
      restTime: "60s",
    },
    {
      id: 10,
      name: "Press Militar",
      muscle: "Hombros",
      difficulty: "Intermedio",
      equipment: "Barra",
      category: "fuerza",
      modelPath: "/models/shoulder-press.glb",
      description: "Ejercicio compuesto para hombros y tríceps",
      reps: "8-12",
      sets: "3-4",
      restTime: "90s",
    },
    {
      id: 11,
      name: "Remo con Barra",
      muscle: "Espalda",
      difficulty: "Intermedio",
      equipment: "Barra",
      category: "fuerza",
      modelPath: "/models/barbell-row.glb",
      description: "Ejercicio para espalda media y dorsales",
      reps: "8-12",
      sets: "3-4",
      restTime: "90s",
    },
    {
      id: 12,
      name: "Mountain Climbers",
      muscle: "Core / Cardio",
      difficulty: "Intermedio",
      equipment: "Sin equipo",
      category: "cardio",
      modelPath: "/models/mountain-climber.glb",
      description: "Ejercicio dinámico para core y cardio",
      reps: "20-30",
      sets: "3",
      restTime: "60s",
    },
  ]

  const categories = [
    { id: "all", label: "Todos" },
    { id: "fuerza", label: "Fuerza" },
    { id: "cardio", label: "Cardio" },
    { id: "core", label: "Core" },
  ]

  const filteredExercises = exercises.filter((exercise) => {
    const matchesSearch =
      exercise.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      exercise.muscle.toLowerCase().includes(searchTerm.toLowerCase())
    const matchesCategory = selectedCategory === "all" || exercise.category === selectedCategory
    return matchesSearch && matchesCategory
  })

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case "Principiante":
        return "bg-yellow-100 text-yellow-700 border-yellow-200"
      case "Intermedio":
        return "bg-yellow-100 text-yellow-700 border-yellow-200"
      case "Avanzado":
        return "bg-red-100 text-red-700 border-red-200"
      default:
        return "bg-gray-100 text-gray-700 border-gray-200"
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-6 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Biblioteca de Ejercicios</h1>
          <p className="text-lg text-gray-600">Explora nuestra colección de ejercicios con modelos 3D interactivos</p>
        </div>

        {/* Search and Filters */}
        <div className="bg-white rounded-2xl shadow-md border border-gray-100 p-6 mb-8">
          <div className="flex flex-col md:flex-row gap-4">
            {/* Search */}
            <div className="flex-1 relative">
              <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                placeholder="Buscar ejercicios o músculos..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-12 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl text-gray-900 focus:outline-none focus:ring-2 focus:ring-yellow-400"
              />
            </div>

            {/* Category Filter */}
            <div className="flex items-center gap-2 overflow-x-auto">
              {categories.map((category) => (
                <button
                  key={category.id}
                  onClick={() => setSelectedCategory(category.id)}
                  className={`px-5 py-3 rounded-xl font-semibold whitespace-nowrap transition-all ${
                    selectedCategory === category.id
                      ? "bg-yellow-400 text-black shadow-lg shadow-yellow-400/25"
                      : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                  }`}
                >
                  {category.label}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Exercise Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {filteredExercises.map((exercise) => (
            <div
              key={exercise.id}
              onClick={() => setSelectedExercise(exercise)}
              className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden hover:shadow-xl transition-all cursor-pointer group"
            >
              {/* 3D Model Placeholder */}
              <div className="h-48 bg-gradient-to-br from-gray-800 to-gray-900 relative overflow-hidden">
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="text-center">
                    <Dumbbell className="w-12 h-12 text-white/50 mx-auto mb-2" />
                    <p className="text-white/70 text-sm font-medium">Modelo 3D</p>
                    <p className="text-white/50 text-xs">Espacio para modelo interactivo</p>
                  </div>
                </div>
                <div className="absolute top-4 right-4 w-10 h-10 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <Play className="w-5 h-5 text-white" />
                </div>
              </div>

              {/* Exercise Info */}
              <div className="p-5">
                <div className="flex items-center gap-2 mb-3">
                  <span
                    className={`px-3 py-1 rounded-full text-xs font-semibold border ${getDifficultyColor(exercise.difficulty)}`}
                  >
                    {exercise.difficulty}
                  </span>
                  <span className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-xs font-semibold">
                    {exercise.muscle}
                  </span>
                </div>

                <h3 className="text-lg font-bold text-gray-900 mb-2">{exercise.name}</h3>

                <div className="flex items-center gap-4 text-sm text-gray-600 mb-3">
                  <div className="flex items-center gap-1">
                    <TrendingUp className="w-4 h-4" />
                    <span>{exercise.sets} series</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Clock className="w-4 h-4" />
                    <span>{exercise.reps} reps</span>
                  </div>
                </div>

                <p className="text-sm text-gray-600 line-clamp-2">{exercise.description}</p>

                <button className="w-full mt-4 flex items-center justify-center gap-2 px-4 py-2 bg-yellow-50 text-yellow-600 font-semibold rounded-xl hover:bg-yellow-100 transition-colors">
                  <Info className="w-4 h-4" />
                  Ver Detalles
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* No Results */}
        {filteredExercises.length === 0 && (
          <div className="text-center py-16">
            <Filter className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-xl font-bold text-gray-900 mb-2">No se encontraron ejercicios</h3>
            <p className="text-gray-600">Intenta ajustar los filtros o el término de búsqueda</p>
          </div>
        )}
      </div>

      {/* Exercise Detail Modal */}
      {selectedExercise && (
        <div
          className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
          onClick={() => setSelectedExercise(null)}
        >
          <div
            className="bg-white rounded-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            {/* 3D Model Section */}
            <div className="h-80 bg-gradient-to-br from-gray-800 to-gray-900 relative">
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="text-center">
                  <Dumbbell className="w-20 h-20 text-white/50 mx-auto mb-4" />
                  <p className="text-white text-lg font-semibold mb-2">Modelo 3D Interactivo</p>
                  <p className="text-white/70 text-sm">Espacio reservado para el modelo 3D del ejercicio</p>
                  <p className="text-white/50 text-xs mt-2">Ruta: {selectedExercise.modelPath}</p>
                </div>
              </div>
              <button
                onClick={() => setSelectedExercise(null)}
                className="absolute top-4 right-4 w-10 h-10 bg-white/20 backdrop-blur-sm hover:bg-white/30 rounded-full flex items-center justify-center text-white transition-colors"
              >
                ✕
              </button>
            </div>

            {/* Exercise Details */}
            <div className="p-8">
              <div className="flex items-start justify-between mb-6">
                <div>
                  <h2 className="text-3xl font-bold text-gray-900 mb-2">{selectedExercise.name}</h2>
                  <div className="flex items-center gap-2">
                    <span
                      className={`px-3 py-1 rounded-full text-sm font-semibold border ${getDifficultyColor(selectedExercise.difficulty)}`}
                    >
                      {selectedExercise.difficulty}
                    </span>
                    <span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-sm font-semibold">
                      {selectedExercise.muscle}
                    </span>
                    <span className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm font-semibold">
                      {selectedExercise.equipment}
                    </span>
                  </div>
                </div>
              </div>

              <p className="text-gray-700 text-lg mb-6">{selectedExercise.description}</p>

              {/* Exercise Parameters */}
              <div className="grid grid-cols-3 gap-4 mb-8">
                <div className="p-4 bg-yellow-50 rounded-xl border border-yellow-200">
                  <div className="flex items-center gap-2 mb-2">
                    <TrendingUp className="w-5 h-5 text-yellow-600" />
                    <p className="text-sm font-semibold text-yellow-900">Series</p>
                  </div>
                  <p className="text-2xl font-bold text-gray-900">{selectedExercise.sets}</p>
                </div>

                <div className="p-4 bg-blue-50 rounded-xl border border-blue-200">
                  <div className="flex items-center gap-2 mb-2">
                    <Dumbbell className="w-5 h-5 text-blue-600" />
                    <p className="text-sm font-semibold text-blue-900">Repeticiones</p>
                  </div>
                  <p className="text-2xl font-bold text-gray-900">{selectedExercise.reps}</p>
                </div>

                <div className="p-4 bg-purple-50 rounded-xl border border-purple-200">
                  <div className="flex items-center gap-2 mb-2">
                    <Clock className="w-5 h-5 text-purple-600" />
                    <p className="text-sm font-semibold text-purple-900">Descanso</p>
                  </div>
                  <p className="text-2xl font-bold text-gray-900">{selectedExercise.restTime}</p>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex gap-4">
                <button className="flex-1 px-6 py-3 bg-yellow-400 text-black font-semibold rounded-xl hover:bg-yellow-500 transition-colors">
                  Agregar a Rutina
                </button>
                <button className="px-6 py-3 bg-gray-200 text-gray-700 font-semibold rounded-xl hover:bg-gray-300 transition-colors">
                  Compartir
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Ejercicios

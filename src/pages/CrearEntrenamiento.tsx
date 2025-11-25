"use client"

import type React from "react"
import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { Plus, Trash2, Save, Eye, Clock, Dumbbell, Target, Users } from "lucide-react"

interface SelectedExercise {
  id: number
  name: string
  sets: string
  reps: string
  restTime: string
  muscle: string
}

const CrearEntrenamiento: React.FC = () => {
  const navigate = useNavigate()
  const [workoutName, setWorkoutName] = useState("")
  const [workoutDescription, setWorkoutDescription] = useState("")
  const [difficulty, setDifficulty] = useState("Intermedio")
  const [category, setCategory] = useState("fuerza")
  const [duration, setDuration] = useState(45)
  const [targetAudience, setTargetAudience] = useState("general")
  const [selectedExercises, setSelectedExercises] = useState<SelectedExercise[]>([])
  const [showExerciseSelector, setShowExerciseSelector] = useState(false)

  // Sample exercises (in real app, fetch from database)
  const availableExercises = [
    { id: 1, name: "Press de Banca", muscle: "Pecho", sets: "3-4", reps: "8-12", restTime: "90s" },
    { id: 2, name: "Sentadilla", muscle: "Piernas", sets: "4", reps: "10-15", restTime: "2min" },
    { id: 3, name: "Peso Muerto", muscle: "Espalda", sets: "3-4", reps: "6-10", restTime: "3min" },
    { id: 4, name: "Dominadas", muscle: "Espalda", sets: "3-4", reps: "6-12", restTime: "2min" },
    { id: 5, name: "Flexiones", muscle: "Pecho", sets: "3", reps: "12-20", restTime: "60s" },
    { id: 6, name: "Plancha", muscle: "Core", sets: "3", reps: "30-60s", restTime: "60s" },
    { id: 7, name: "Curl de Bíceps", muscle: "Bíceps", sets: "3", reps: "10-15", restTime: "60s" },
    { id: 8, name: "Press Militar", muscle: "Hombros", sets: "3-4", reps: "8-12", restTime: "90s" },
  ]

  const addExercise = (exercise: (typeof availableExercises)[0]) => {
    setSelectedExercises([...selectedExercises, exercise])
    setShowExerciseSelector(false)
  }

  const removeExercise = (index: number) => {
    setSelectedExercises(selectedExercises.filter((_, i) => i !== index))
  }

  const handlePublish = () => {
    if (!workoutName || selectedExercises.length === 0) {
      alert("Por favor completa el nombre y agrega al menos un ejercicio")
      return
    }

    // In real app, save to database
    console.log("Publishing workout:", {
      workoutName,
      workoutDescription,
      difficulty,
      category,
      duration,
      targetAudience,
      exercises: selectedExercises,
    })

    alert("Entrenamiento publicado exitosamente!")
    navigate("/dashboard/entrenamiento")
  }

  return (
    <div className="min-h-screen bg-black">
      <div className="max-w-6xl mx-auto px-6 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-yellow-400 to-yellow-600 bg-clip-text text-transparent mb-2">
            Crear Entrenamiento
          </h1>
          <p className="text-lg text-gray-400">Diseña y publica tu propio entrenamiento para la comunidad</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left Column - Workout Details */}
          <div className="lg:col-span-2 space-y-6">
            {/* Basic Info */}
            <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-8">
              <h2 className="text-2xl font-bold text-white mb-6">Información Básica</h2>

              <div className="space-y-5">
                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-2">Nombre del Entrenamiento*</label>
                  <input
                    type="text"
                    value={workoutName}
                    onChange={(e) => setWorkoutName(e.target.value)}
                    placeholder="Ej: Full Body Strength Training"
                    className="w-full px-4 py-3 bg-black border border-zinc-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-2">Descripción</label>
                  <textarea
                    value={workoutDescription}
                    onChange={(e) => setWorkoutDescription(e.target.value)}
                    placeholder="Describe el objetivo y beneficios de este entrenamiento..."
                    rows={4}
                    className="w-full px-4 py-3 bg-black border border-zinc-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500 resize-none"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Dificultad</label>
                    <select
                      value={difficulty}
                      onChange={(e) => setDifficulty(e.target.value)}
                      className="w-full px-4 py-3 bg-black border border-zinc-800 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500"
                    >
                      <option>Principiante</option>
                      <option>Intermedio</option>
                      <option>Avanzado</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Categoría</label>
                    <select
                      value={category}
                      onChange={(e) => setCategory(e.target.value)}
                      className="w-full px-4 py-3 bg-black border border-zinc-800 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500"
                    >
                      <option value="fuerza">Fuerza</option>
                      <option value="cardio">Cardio</option>
                      <option value="flexibilidad">Flexibilidad</option>
                      <option value="core">Core</option>
                    </select>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Duración (minutos)</label>
                    <input
                      type="number"
                      value={duration}
                      onChange={(e) => setDuration(Number(e.target.value))}
                      className="w-full px-4 py-3 bg-black border border-zinc-800 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Audiencia</label>
                    <select
                      value={targetAudience}
                      onChange={(e) => setTargetAudience(e.target.value)}
                      className="w-full px-4 py-3 bg-black border border-zinc-800 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500"
                    >
                      <option value="general">General</option>
                      <option value="hombres">Hombres</option>
                      <option value="mujeres">Mujeres</option>
                      <option value="seniors">Adultos mayores</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>

            {/* Exercise List */}
            <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-8">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-white">Ejercicios ({selectedExercises.length})</h2>
                <button
                  onClick={() => setShowExerciseSelector(true)}
                  className="flex items-center gap-2 px-4 py-2 bg-yellow-500 text-black font-semibold rounded-xl hover:bg-yellow-400 transition-colors"
                >
                  <Plus className="w-5 h-5" />
                  Agregar Ejercicio
                </button>
              </div>

              {selectedExercises.length === 0 ? (
                <div className="text-center py-12 border-2 border-dashed border-zinc-800 rounded-xl">
                  <Dumbbell className="w-12 h-12 text-gray-600 mx-auto mb-3" />
                  <p className="text-gray-400 mb-4">No has agregado ejercicios aún</p>
                  <button
                    onClick={() => setShowExerciseSelector(true)}
                    className="px-4 py-2 bg-yellow-500 text-black font-semibold rounded-xl hover:bg-yellow-400 transition-colors"
                  >
                    Agregar Primer Ejercicio
                  </button>
                </div>
              ) : (
                <div className="space-y-4">
                  {selectedExercises.map((exercise, index) => (
                    <div
                      key={index}
                      className="flex items-center justify-between p-4 bg-black rounded-xl border border-zinc-800"
                    >
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-2">
                          <span className="w-8 h-8 bg-yellow-500 text-black rounded-full flex items-center justify-center font-bold text-sm">
                            {index + 1}
                          </span>
                          <h3 className="font-bold text-white">{exercise.name}</h3>
                          <span className="px-2 py-1 bg-yellow-500/20 text-yellow-400 rounded-lg text-xs font-semibold">
                            {exercise.muscle}
                          </span>
                        </div>
                        <div className="flex items-center gap-4 ml-11 text-sm text-gray-400">
                          <span>Series: {exercise.sets}</span>
                          <span>Reps: {exercise.reps}</span>
                          <span>Descanso: {exercise.restTime}</span>
                        </div>
                      </div>
                      <button
                        onClick={() => removeExercise(index)}
                        className="p-2 text-red-400 hover:bg-red-500/10 rounded-xl transition-colors"
                      >
                        <Trash2 className="w-5 h-5" />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Right Column - Preview and Actions */}
          <div className="space-y-6">
            {/* Preview Card */}
            <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-6 sticky top-6">
              <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
                <Eye className="w-5 h-5 text-yellow-500" />
                Vista Previa
              </h3>

              <div className="space-y-4">
                <div>
                  <p className="text-sm text-gray-400 mb-1">Nombre</p>
                  <p className="font-semibold text-white">{workoutName || "Sin nombre"}</p>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="p-3 bg-yellow-500/10 rounded-xl border border-yellow-500/20">
                    <Clock className="w-5 h-5 text-yellow-500 mb-1" />
                    <p className="text-xs text-gray-400">Duración</p>
                    <p className="font-bold text-white">{duration} min</p>
                  </div>

                  <div className="p-3 bg-yellow-500/10 rounded-xl border border-yellow-500/20">
                    <Dumbbell className="w-5 h-5 text-yellow-500 mb-1" />
                    <p className="text-xs text-gray-400">Ejercicios</p>
                    <p className="font-bold text-white">{selectedExercises.length}</p>
                  </div>

                  <div className="p-3 bg-yellow-500/10 rounded-xl border border-yellow-500/20">
                    <Target className="w-5 h-5 text-yellow-500 mb-1" />
                    <p className="text-xs text-gray-400">Dificultad</p>
                    <p className="font-bold text-white text-sm">{difficulty}</p>
                  </div>

                  <div className="p-3 bg-yellow-500/10 rounded-xl border border-yellow-500/20">
                    <Users className="w-5 h-5 text-yellow-500 mb-1" />
                    <p className="text-xs text-gray-400">Audiencia</p>
                    <p className="font-bold text-white text-sm capitalize">{targetAudience}</p>
                  </div>
                </div>

                <div className="pt-4 border-t border-zinc-800 space-y-3">
                  <button
                    onClick={handlePublish}
                    className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-yellow-500 text-black font-semibold rounded-xl hover:bg-yellow-400 transition-colors"
                  >
                    <Save className="w-5 h-5" />
                    Publicar Entrenamiento
                  </button>

                  <button
                    onClick={() => navigate("/dashboard/entrenamiento")}
                    className="w-full px-4 py-3 bg-zinc-800 text-gray-300 font-semibold rounded-xl hover:bg-zinc-700 transition-colors"
                  >
                    Cancelar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Exercise Selector Modal */}
      {showExerciseSelector && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"
          onClick={() => setShowExerciseSelector(false)}
        >
          <div
            className="bg-zinc-900 rounded-2xl max-w-2xl w-full max-h-[80vh] overflow-hidden border border-zinc-800"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-6 border-b border-zinc-800">
              <h2 className="text-2xl font-bold text-white">Seleccionar Ejercicio</h2>
              <p className="text-gray-400">Elige un ejercicio para agregar al entrenamiento</p>
            </div>

            <div className="overflow-y-auto max-h-[60vh] p-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {availableExercises.map((exercise) => (
                  <div
                    key={exercise.id}
                    onClick={() => addExercise(exercise)}
                    className="p-4 bg-black rounded-xl border border-zinc-800 hover:border-yellow-500 hover:bg-yellow-500/5 cursor-pointer transition-all group"
                  >
                    <div className="flex items-center justify-between mb-2">
                      <h3 className="font-bold text-white group-hover:text-yellow-400">{exercise.name}</h3>
                      <Plus className="w-5 h-5 text-gray-600 group-hover:text-yellow-500" />
                    </div>
                    <p className="text-sm text-gray-400 mb-2">{exercise.muscle}</p>
                    <div className="flex items-center gap-2 text-xs text-gray-500">
                      <span>{exercise.sets} series</span>
                      <span>•</span>
                      <span>{exercise.reps} reps</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="p-6 border-t border-zinc-800">
              <button
                onClick={() => setShowExerciseSelector(false)}
                className="w-full px-4 py-3 bg-zinc-800 text-gray-300 font-semibold rounded-xl hover:bg-zinc-700 transition-colors"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default CrearEntrenamiento

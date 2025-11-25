import type React from "react"
import { TrendingUp, Trophy, Target, Flame, Activity } from "lucide-react"

const Seguimiento: React.FC = () => {
  const weeklyData = [
    { day: "Lun", completed: true, workouts: 1 },
    { day: "Mar", completed: true, workouts: 1 },
    { day: "Mié", completed: false, workouts: 0 },
    { day: "Jue", completed: true, workouts: 2 },
    { day: "Vie", completed: true, workouts: 1 },
    { day: "Sáb", completed: false, workouts: 0 },
    { day: "Dom", completed: false, workouts: 0 },
  ]

  const recentWorkouts = [
    { name: "Full Body Workout", date: "2024-01-25", duration: 45, calories: 350 },
    { name: "Cardio HIIT", date: "2024-01-24", duration: 30, calories: 400 },
    { name: "Upper Body Strength", date: "2024-01-23", duration: 40, calories: 300 },
    { name: "Yoga Flow", date: "2024-01-22", duration: 25, calories: 150 },
  ]

  return (
    <div className="min-h-screen bg-black">
      <div className="max-w-7xl mx-auto px-6 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">Seguimiento de Entrenamiento</h1>
          <p className="text-lg text-gray-400">Monitorea tu progreso y mantén tu racha activa</p>
        </div>

        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-gradient-to-br from-yellow-500 to-yellow-600 rounded-2xl p-6 text-black shadow-lg">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-black/10 rounded-xl flex items-center justify-center">
                <Flame className="w-6 h-6" />
              </div>
              <span className="text-sm font-semibold bg-black/10 px-3 py-1 rounded-full">Esta semana</span>
            </div>
            <p className="text-4xl font-bold mb-1">5</p>
            <p className="text-black/80">Días activos</p>
          </div>

          <div className="bg-zinc-900 rounded-2xl p-6 shadow-md border border-zinc-800">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                <Trophy className="w-6 h-6 text-yellow-500" />
              </div>
            </div>
            <p className="text-4xl font-bold text-white mb-1">12</p>
            <p className="text-gray-400">Entrenamientos completados</p>
          </div>

          <div className="bg-zinc-900 rounded-2xl p-6 shadow-md border border-zinc-800">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                <Activity className="w-6 h-6 text-yellow-500" />
              </div>
            </div>
            <p className="text-4xl font-bold text-white mb-1">420</p>
            <p className="text-gray-400">Minutos totales</p>
          </div>

          <div className="bg-zinc-900 rounded-2xl p-6 shadow-md border border-zinc-800">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                <TrendingUp className="w-6 h-6 text-yellow-500" />
              </div>
            </div>
            <p className="text-4xl font-bold text-white mb-1">3,500</p>
            <p className="text-gray-400">Calorías quemadas</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Weekly Activity */}
          <div className="lg:col-span-2 bg-zinc-900 rounded-2xl shadow-md border border-zinc-800 p-8">
            <h2 className="text-2xl font-bold text-white mb-6">Actividad Semanal</h2>

            <div className="flex items-end justify-between gap-3 h-64 mb-6">
              {weeklyData.map((day, index) => (
                <div key={index} className="flex-1 flex flex-col items-center gap-3">
                  <div className="flex-1 w-full flex items-end">
                    <div
                      className={`w-full rounded-t-xl transition-all ${day.completed ? "bg-yellow-500" : "bg-zinc-800"}`}
                      style={{ height: `${day.workouts * 50}%` || "10%" }}
                    ></div>
                  </div>
                  <div className="text-center">
                    <p className="text-sm font-semibold text-white">{day.day}</p>
                    {day.completed && <div className="w-2 h-2 bg-yellow-500 rounded-full mx-auto mt-1"></div>}
                  </div>
                </div>
              ))}
            </div>

            <div className="flex items-center justify-center gap-6 pt-6 border-t border-zinc-800">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 bg-yellow-500 rounded"></div>
                <span className="text-sm text-gray-400">Completado</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 bg-zinc-800 rounded"></div>
                <span className="text-sm text-gray-400">Pendiente</span>
              </div>
            </div>
          </div>

          {/* Goals Progress */}
          <div className="space-y-6">
            <div className="bg-zinc-900 rounded-2xl shadow-md border border-zinc-800 p-6">
              <h3 className="text-xl font-bold text-white mb-6">Objetivos del Mes</h3>

              <div className="space-y-4">
                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="font-semibold text-white">Entrenamientos</span>
                    <span className="text-gray-400">12/20</span>
                  </div>
                  <div className="w-full h-2 bg-zinc-800 rounded-full overflow-hidden">
                    <div className="h-full bg-yellow-500 rounded-full" style={{ width: "60%" }}></div>
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="font-semibold text-white">Calorías</span>
                    <span className="text-gray-400">3,500/5,000</span>
                  </div>
                  <div className="w-full h-2 bg-zinc-800 rounded-full overflow-hidden">
                    <div className="h-full bg-yellow-500 rounded-full" style={{ width: "70%" }}></div>
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="font-semibold text-white">Minutos activos</span>
                    <span className="text-gray-400">420/600</span>
                  </div>
                  <div className="w-full h-2 bg-zinc-800 rounded-full overflow-hidden">
                    <div className="h-full bg-yellow-500 rounded-full" style={{ width: "70%" }}></div>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-gradient-to-br from-yellow-500 to-yellow-600 rounded-2xl p-6 text-black">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-12 h-12 bg-black/10 rounded-xl flex items-center justify-center">
                  <Target className="w-6 h-6" />
                </div>
                <div>
                  <p className="text-sm text-black/70">Racha actual</p>
                  <p className="text-2xl font-bold">7 días</p>
                </div>
              </div>
              <p className="text-sm text-black/80">¡Sigue así! Estás en tu mejor momento.</p>
            </div>
          </div>
        </div>

        {/* Recent Workouts */}
        <div className="mt-6 bg-zinc-900 rounded-2xl shadow-md border border-zinc-800 p-8">
          <h2 className="text-2xl font-bold text-white mb-6">Entrenamientos Recientes</h2>

          <div className="space-y-4">
            {recentWorkouts.map((workout, index) => (
              <div
                key={index}
                className="flex items-center justify-between p-4 bg-black/50 rounded-xl hover:bg-black/70 transition-colors border border-zinc-800"
              >
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                    <Trophy className="w-6 h-6 text-yellow-500" />
                  </div>
                  <div>
                    <p className="font-semibold text-white">{workout.name}</p>
                    <p className="text-sm text-gray-400">{workout.date}</p>
                  </div>
                </div>
                <div className="flex items-center gap-6">
                  <div className="text-right">
                    <p className="text-sm text-gray-400">Duración</p>
                    <p className="font-semibold text-white">{workout.duration} min</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-gray-400">Calorías</p>
                    <p className="font-semibold text-white">{workout.calories} cal</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default Seguimiento

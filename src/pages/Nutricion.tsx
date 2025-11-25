"use client"

import { useState } from "react"
import { Apple, Utensils, Calculator, BookOpen, ChevronRight, Plus } from "lucide-react"

function Nutricion() {
  const [dailyCalories] = useState(2200)
  const [consumedCalories] = useState(1650)

  const macros = [
    { name: "Proteínas", current: 120, goal: 150, unit: "g", color: "bg-yellow-500" },
    { name: "Carbohidratos", current: 180, goal: 250, unit: "g", color: "bg-white" },
    { name: "Grasas", current: 50, goal: 70, unit: "g", color: "bg-yellow-600" },
  ]

  const meals = [
    { name: "Desayuno", time: "08:00", calories: 450, protein: 25, icon: "🌅" },
    { name: "Almuerzo", time: "13:00", calories: 650, protein: 45, icon: "☀️" },
    { name: "Snack", time: "16:30", calories: 200, protein: 15, icon: "🍎" },
    { name: "Cena", time: "20:00", calories: 550, protein: 35, icon: "🌙" },
  ]

  const recommendedMeals = [
    { name: "Ensalada de Pollo Grillado", calories: 420, protein: 38, carbs: 25, fats: 18, image: "🥗" },
    { name: "Salmón con Quinoa", calories: 580, protein: 45, carbs: 42, fats: 22, image: "🐟" },
    { name: "Bowl de Proteína Vegetal", calories: 390, protein: 28, carbs: 48, fats: 12, image: "🥙" },
  ]

  return (
    <div className="min-h-screen bg-black text-white">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2 bg-gradient-to-r from-yellow-400 to-yellow-500 bg-clip-text text-transparent">
            Nutrición
          </h1>
          <p className="text-gray-400">Controla tu alimentación y alcanza tus objetivos</p>
        </div>

        {/* Calorías Diarias */}
        <div className="bg-zinc-900 rounded-2xl p-6 mb-6 border border-zinc-800 shadow-xl">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <div className="bg-yellow-500/20 p-3 rounded-xl">
                <Apple className="w-6 h-6 text-yellow-400" />
              </div>
              <div>
                <h3 className="text-lg font-semibold">Calorías Diarias</h3>
                <p className="text-sm text-gray-400">Meta: {dailyCalories} kcal</p>
              </div>
            </div>
          </div>

          <div className="relative pt-1">
            <div className="flex mb-2 items-center justify-between">
              <div>
                <span className="text-3xl font-bold text-yellow-400">{consumedCalories}</span>
                <span className="text-gray-400 ml-1">/ {dailyCalories} kcal</span>
              </div>
              <span className="text-sm font-semibold text-gray-400">{dailyCalories - consumedCalories} restantes</span>
            </div>
            <div className="overflow-hidden h-3 text-xs flex rounded-full bg-zinc-800">
              <div
                style={{ width: `${(consumedCalories / dailyCalories) * 100}%` }}
                className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-gradient-to-r from-yellow-400 to-yellow-500"
              />
            </div>
          </div>
        </div>

        {/* Macronutrientes */}
        <div className="grid md:grid-cols-3 gap-4 mb-6">
          {macros.map((macro) => {
            const percentage = (macro.current / macro.goal) * 100
            return (
              <div
                key={macro.name}
                className="bg-zinc-900 rounded-xl p-5 border border-zinc-800 hover:border-yellow-500/50 transition-all"
              >
                <h4 className="text-sm text-gray-400 mb-2">{macro.name}</h4>
                <div className="flex items-end gap-2 mb-3">
                  <span className="text-2xl font-bold">{macro.current}</span>
                  <span className="text-gray-400 mb-1">
                    / {macro.goal}
                    {macro.unit}
                  </span>
                </div>
                <div className="w-full bg-zinc-800 rounded-full h-2">
                  <div
                    className={`${macro.color} h-2 rounded-full transition-all duration-300`}
                    style={{ width: `${percentage}%` }}
                  />
                </div>
              </div>
            )
          })}
        </div>

        {/* Comidas del Día */}
        <div className="bg-zinc-900 rounded-2xl p-6 mb-6 border border-zinc-800">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="bg-yellow-500/20 p-3 rounded-xl">
                <Utensils className="w-6 h-6 text-yellow-400" />
              </div>
              <h3 className="text-xl font-bold">Comidas de Hoy</h3>
            </div>
            <button className="flex items-center gap-2 bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded-lg transition-colors font-semibold">
              <Plus className="w-4 h-4" />
              Agregar
            </button>
          </div>

          <div className="space-y-3">
            {meals.map((meal) => (
              <div
                key={meal.name}
                className="flex items-center justify-between p-4 bg-black/50 rounded-xl hover:bg-black/70 transition-colors cursor-pointer group border border-zinc-800"
              >
                <div className="flex items-center gap-4">
                  <span className="text-3xl">{meal.icon}</span>
                  <div>
                    <h4 className="font-semibold">{meal.name}</h4>
                    <p className="text-sm text-gray-400">{meal.time}</p>
                  </div>
                </div>
                <div className="flex items-center gap-6">
                  <div className="text-right">
                    <p className="font-semibold text-yellow-400">{meal.calories} kcal</p>
                    <p className="text-xs text-gray-400">{meal.protein}g proteína</p>
                  </div>
                  <ChevronRight className="w-5 h-5 text-gray-600 group-hover:text-yellow-400 transition-colors" />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Comidas Recomendadas */}
        <div className="mb-6">
          <div className="flex items-center gap-3 mb-4">
            <div className="bg-yellow-500/20 p-3 rounded-xl">
              <BookOpen className="w-6 h-6 text-yellow-400" />
            </div>
            <h3 className="text-xl font-bold">Comidas Recomendadas</h3>
          </div>

          <div className="grid md:grid-cols-3 gap-4">
            {recommendedMeals.map((meal) => (
              <div
                key={meal.name}
                className="bg-zinc-900 rounded-xl overflow-hidden border border-zinc-800 hover:border-yellow-500/50 transition-all hover:scale-105 cursor-pointer group"
              >
                <div className="bg-gradient-to-br from-zinc-800 to-zinc-900 h-32 flex items-center justify-center text-6xl">
                  {meal.image}
                </div>
                <div className="p-4">
                  <h4 className="font-bold mb-2 group-hover:text-yellow-400 transition-colors">{meal.name}</h4>
                  <div className="flex justify-between text-sm text-gray-400 mb-3">
                    <span>{meal.calories} kcal</span>
                    <span>P: {meal.protein}g</span>
                  </div>
                  <div className="flex justify-between text-xs text-gray-500">
                    <span>C: {meal.carbs}g</span>
                    <span>G: {meal.fats}g</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Calculadora Nutricional */}
        <div className="bg-gradient-to-br from-yellow-500/10 to-yellow-600/10 rounded-2xl p-6 border border-yellow-500/20">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="bg-yellow-500/20 p-4 rounded-xl">
                <Calculator className="w-8 h-8 text-yellow-400" />
              </div>
              <div>
                <h3 className="text-xl font-bold mb-1">Calculadora Nutricional</h3>
                <p className="text-gray-400">Calcula tus necesidades calóricas personalizadas</p>
              </div>
            </div>
            <button className="bg-yellow-500 hover:bg-yellow-600 text-black px-6 py-3 rounded-lg font-semibold transition-colors">
              Calcular
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Nutricion

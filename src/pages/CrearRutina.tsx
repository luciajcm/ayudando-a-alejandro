import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, Save, Trash2, Dumbbell } from "lucide-react";
import { getExercises, createRoutine } from "../api/api";

const CrearRutina = () => {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [desc, setDesc] = useState("");
  
  // Lista completa de ejercicios (desde BD)
  const [allExercises, setAllExercises] = useState<any[]>([]);
  
  // Ejercicios seleccionados para esta rutina
  const [selectedExercises, setSelectedExercises] = useState<any[]>([]);

  useEffect(() => {
    // Cargar ejercicios disponibles (tus modelos 3D)
    getExercises().then(setAllExercises).catch(console.error);
  }, []);

  const addExercise = (exercise: any) => {
    // Añadimos el ejercicio con valores por defecto
    setSelectedExercises([
        ...selectedExercises, 
        { ...exercise, sets: 3, reps: 10, weight: 0, tempId: Date.now() }
    ]);
  };

  const updateExerciseDetail = (index: number, field: string, value: any) => {
    const updated = [...selectedExercises];
    updated[index][field] = value;
    setSelectedExercises(updated);
  };

  const removeExercise = (index: number) => {
    const updated = [...selectedExercises];
    updated.splice(index, 1);
    setSelectedExercises(updated);
  };

  const handleSave = async () => {
    if(!name) return alert("Ponle un nombre a la rutina");
    
    const payload = {
        name,
        description: desc,
        exercises: selectedExercises.map(e => ({
            exerciseId: e.id, // ID real de la BD
            sets: Number(e.sets),
            reps: Number(e.reps),
            weight: Number(e.weight)
        }))
    };

    try {
        await createRoutine(payload);
        alert("Rutina creada!");
        navigate("/dashboard/entrenamiento");
    } catch (e) {
        alert("Error creando rutina");
    }
  };

  return (
    <div className="min-h-screen bg-black p-8 text-white">
      <h1 className="text-3xl font-bold text-yellow-500 mb-6">Nueva Rutina</h1>
      
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* COLUMNA 1: Detalles y Lista Actual */}
        <div className="lg:col-span-2 space-y-6">
            <div className="bg-zinc-900 p-6 rounded-xl border border-zinc-800">
                <label className="block text-sm text-gray-400 mb-1">Nombre de la Rutina</label>
                <input value={name} onChange={e => setName(e.target.value)} className="w-full bg-black border border-zinc-700 rounded-lg p-3 text-white mb-4" placeholder="Ej: Pierna Destructora" />
                
                <label className="block text-sm text-gray-400 mb-1">Descripción</label>
                <textarea value={desc} onChange={e => setDesc(e.target.value)} className="w-full bg-black border border-zinc-700 rounded-lg p-3 text-white" placeholder="Objetivo de esta rutina..." />
            </div>

            <div className="space-y-4">
                {selectedExercises.map((item, idx) => (
                    <div key={item.tempId} className="bg-zinc-900 p-4 rounded-xl border border-zinc-800 flex flex-col md:flex-row gap-4 items-center">
                        <div className="flex-1">
                            <h4 className="font-bold text-lg">{item.name}</h4>
                            <p className="text-xs text-gray-500 uppercase">{item.muscle}</p>
                        </div>
                        <div className="flex gap-2">
                            <div>
                                <label className="text-xs text-gray-500 block">Series</label>
                                <input type="number" value={item.sets} onChange={e => updateExerciseDetail(idx, 'sets', e.target.value)} className="w-16 bg-black border border-zinc-700 rounded p-1 text-center" />
                            </div>
                            <div>
                                <label className="text-xs text-gray-500 block">Reps</label>
                                <input type="number" value={item.reps} onChange={e => updateExerciseDetail(idx, 'reps', e.target.value)} className="w-16 bg-black border border-zinc-700 rounded p-1 text-center" />
                            </div>
                            <div>
                                <label className="text-xs text-gray-500 block">Peso(kg)</label>
                                <input type="number" value={item.weight} onChange={e => updateExerciseDetail(idx, 'weight', e.target.value)} className="w-16 bg-black border border-zinc-700 rounded p-1 text-center" />
                            </div>
                        </div>
                        <button onClick={() => removeExercise(idx)} className="p-2 text-red-500 hover:bg-red-500/10 rounded-lg">
                            <Trash2 className="w-5 h-5" />
                        </button>
                    </div>
                ))}
                {selectedExercises.length === 0 && (
                    <div className="text-center p-10 border-2 border-dashed border-zinc-800 rounded-xl text-gray-500">
                        Selecciona ejercicios del panel derecho
                    </div>
                )}
            </div>
            
            <button onClick={handleSave} className="w-full py-4 bg-yellow-500 text-black font-bold rounded-xl hover:bg-yellow-400 transition-colors flex justify-center gap-2">
                <Save className="w-5 h-5" /> Guardar Rutina
            </button>
        </div>

        {/* COLUMNA 2: Selector de Ejercicios */}
        <div className="bg-zinc-900 p-6 rounded-xl border border-zinc-800 h-fit sticky top-6">
            <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
                <Dumbbell className="w-5 h-5 text-yellow-500" /> 
                Biblioteca
            </h3>
            <div className="space-y-2 max-h-[600px] overflow-y-auto pr-2">
                {allExercises.map(ex => (
                    <button 
                        key={ex.id} 
                        onClick={() => addExercise(ex)}
                        className="w-full text-left p-3 hover:bg-zinc-800 rounded-lg border border-transparent hover:border-zinc-700 transition-all flex justify-between items-center group"
                    >
                        <div>
                            <p className="font-medium">{ex.name}</p>
                            <p className="text-xs text-gray-500">{ex.muscle}</p>
                        </div>
                        <Plus className="w-5 h-5 text-yellow-500 opacity-0 group-hover:opacity-100" />
                    </button>
                ))}
                {/* MOCK TEMPORAL SI NO HAY DATOS */}
                {allExercises.length === 0 && [1,2,3].map(i => (
                     <button key={i} onClick={() => addExercise({id: i, name: `Ejercicio ${i}`, muscle: "Pecho"})} className="w-full text-left p-3 hover:bg-zinc-800 rounded-lg border border-transparent hover:border-zinc-700 transition-all flex justify-between items-center group">
                        <div><p className="font-medium">Press Banca {i}</p><p className="text-xs text-gray-500">Pecho</p></div>
                        <Plus className="w-5 h-5 text-yellow-500" />
                    </button>
                ))}
            </div>
        </div>
      </div>
    </div>
  );
};

export default CrearRutina;
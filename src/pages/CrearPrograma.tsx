import React, { useState, FormEvent, JSX } from "react";

type RoutineType = "Fuerza" | "Hipertrofia" | "Resistencia" | "Cardio";

type Exercise = {
    id: string;
    name: string;
    sets?: number;
    reps?: number;
    restSeconds?: number;
    durationSeconds?: number; // para circuitos / cardio intervals
};

export default function CrearPrograma(): JSX.Element {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [routineType, setRoutineType] = useState<RoutineType>("Fuerza");
    const [daysPerWeek, setDaysPerWeek] = useState<number>(3);
    const [durationWeeks, setDurationWeeks] = useState<number>(8);
    const [intensity, setIntensity] = useState<"Baja" | "Media" | "Alta">("Media");
    const [exercises, setExercises] = useState<Exercise[]>([]);
    const [cardioDistanceKm, setCardioDistanceKm] = useState<number | "">("");
    const [cardioDurationMin, setCardioDurationMin] = useState<number | "">("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<string | null>(null);

    function addExercise() {
        setExercises((prev) => [
            ...prev,
            {
                id: Date.now().toString(),
                name: "",
            },
        ]);
    }

    function updateExercise(id: string, patch: Partial<Exercise>) {
        setExercises((prev) => prev.map((ex) => (ex.id === id ? { ...ex, ...patch } : ex)));
    }

    function removeExercise(id: string) {
        setExercises((prev) => prev.filter((ex) => ex.id !== id));
    }

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        setMessage(null);

        if (!title.trim()) {
            setMessage("El título es obligatorio.");
            return;
        }

        if (exercises.length === 0 && routineType !== "Cardio" && routineType !== "Resistencia") {
            setMessage("Agrega al menos un ejercicio para este tipo de rutina.");
            return;
        }

        const payload: any = {
            title,
            description,
            routineType,
            daysPerWeek,
            durationWeeks,
            intensity,
        };

        // Tipo específico
        if (routineType === "Cardio" || routineType === "Resistencia") {
            if (!cardioDurationMin && !cardioDistanceKm) {
                setMessage("Indica duración o distancia para rutinas de cardio/resistencia.");
                return;
            }
            payload.cardio = {
                distanceKm: cardioDistanceKm || null,
                durationMin: cardioDurationMin || null,
            };
            // allow optional exercises as intervals
            if (exercises.length) payload.intervals = exercises;
        } else {
            payload.exercises = exercises.map((ex) => ({
                name: ex.name,
                sets: ex.sets ?? null,
                reps: ex.reps ?? null,
                restSeconds: ex.restSeconds ?? null,
                durationSeconds: ex.durationSeconds ?? null,
            }));
        }

        try {
            setLoading(true);
            const res = await fetch("/api/programas", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "Error al crear programa");
            }

            setMessage("Programa creado correctamente.");
            // limpiar formulario básico
            setTitle("");
            setDescription("");
            setExercises([]);
            setCardioDistanceKm("");
            setCardioDurationMin("");
        } catch (err: any) {
            setMessage(err.message || "Error desconocido");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ maxWidth: 800, margin: "0 auto", padding: 16 }}>
            <h1>Crear Programa</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Título</label>
                    <input value={title} onChange={(e) => setTitle(e.target.value)} required />
                </div>

                <div>
                    <label>Descripción</label>
                    <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
                </div>

                <div>
                    <label>Tipo de rutina</label>
                    <select value={routineType} onChange={(e) => setRoutineType(e.target.value as RoutineType)}>
                        <option>Fuerza</option>
                        <option>Hipertrofia</option>
                        <option>Resistencia</option>
                        <option>Cardio</option>
                    </select>
                </div>

                <div>
                    <label>Días por semana</label>
                    <input
                        type="number"
                        min={1}
                        max={7}
                        value={daysPerWeek}
                        onChange={(e) => setDaysPerWeek(Number(e.target.value))}
                    />
                </div>

                <div>
                    <label>Duración (semanas)</label>
                    <input
                        type="number"
                        min={1}
                        value={durationWeeks}
                        onChange={(e) => setDurationWeeks(Number(e.target.value))}
                    />
                </div>

                <div>
                    <label>Intensidad</label>
                    <select value={intensity} onChange={(e) => setIntensity(e.target.value as any)}>
                        <option>Baja</option>
                        <option>Media</option>
                        <option>Alta</option>
                    </select>
                </div>

                {/* Campos condicionales para Cardio/Resistencia */}
                {(routineType === "Cardio" || routineType === "Resistencia") && (
                    <div style={{ border: "1px solid #ddd", padding: 8, marginTop: 8 }}>
                        <h4>Parámetros de Cardio / Resistencia</h4>
                        <div>
                            <label>Duración (minutos)</label>
                            <input
                                type="number"
                                min={0}
                                value={cardioDurationMin === "" ? "" : cardioDurationMin}
                                onChange={(e) => setCardioDurationMin(e.target.value === "" ? "" : Number(e.target.value))}
                            />
                        </div>
                        <div>
                            <label>Distancia (km)</label>
                            <input
                                type="number"
                                min={0}
                                step="0.1"
                                value={cardioDistanceKm === "" ? "" : cardioDistanceKm}
                                onChange={(e) => setCardioDistanceKm(e.target.value === "" ? "" : Number(e.target.value))}
                            />
                        </div>
                        <p style={{ fontSize: 12, color: "#555" }}>
                            Opcional: añade intervalos o ejercicios si quieres sesiones estructuradas.
                        </p>
                    </div>
                )}

                {/* Lista de ejercicios / intervalos */}
                <div style={{ marginTop: 12 }}>
                    <h4>Ejercicios / Intervalos</h4>
                    {exercises.map((ex, idx) => (
                        <div key={ex.id} style={{ border: "1px solid #eee", padding: 8, marginBottom: 8 }}>
                            <div>
                                <label>Nombre</label>
                                <input value={ex.name} onChange={(e) => updateExercise(ex.id, { name: e.target.value })} />
                            </div>

                            {routineType === "Cardio" || routineType === "Resistencia" ? (
                                <div>
                                    <label>Duración (segundos)</label>
                                    <input
                                        type="number"
                                        min={0}
                                        value={ex.durationSeconds ?? ""}
                                        onChange={(e) =>
                                            updateExercise(ex.id, { durationSeconds: e.target.value === "" ? undefined : Number(e.target.value) })
                                        }
                                    />
                                </div>
                            ) : (
                                <>
                                    <div>
                                        <label>Series</label>
                                        <input
                                            type="number"
                                            min={1}
                                            value={ex.sets ?? ""}
                                            onChange={(e) => updateExercise(ex.id, { sets: e.target.value === "" ? undefined : Number(e.target.value) })}
                                        />
                                    </div>
                                    <div>
                                        <label>Reps</label>
                                        <input
                                            type="number"
                                            min={0}
                                            value={ex.reps ?? ""}
                                            onChange={(e) => updateExercise(ex.id, { reps: e.target.value === "" ? undefined : Number(e.target.value) })}
                                        />
                                    </div>
                                    <div>
                                        <label>Descanso (segundos)</label>
                                        <input
                                            type="number"
                                            min={0}
                                            value={ex.restSeconds ?? ""}
                                            onChange={(e) =>
                                                updateExercise(ex.id, { restSeconds: e.target.value === "" ? undefined : Number(e.target.value) })
                                            }
                                        />
                                    </div>
                                </>
                            )}

                            <div>
                                <button type="button" onClick={() => removeExercise(ex.id)}>
                                    Eliminar
                                </button>
                            </div>
                            <div style={{ fontSize: 12, color: "#666" }}>#{idx + 1}</div>
                        </div>
                    ))}

                    <button type="button" onClick={addExercise}>
                        Añadir ejercicio / intervalo
                    </button>
                </div>

                <div style={{ marginTop: 16 }}>
                    <button type="submit" disabled={loading}>
                        {loading ? "Creando..." : "Crear Programa"}
                    </button>
                </div>

                {message && (
                    <div style={{ marginTop: 12 }}>
                        <strong>{message}</strong>
                    </div>
                )}
            </form>
        </div>
    );
}
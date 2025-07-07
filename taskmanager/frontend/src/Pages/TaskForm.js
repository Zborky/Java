import { useState } from 'react';
import './CSS/TaskForm.css';

const API_URL = 'http://localhost:8080/api/tasks';

/**
 * TaskForm component allows users to create a new task.
 *
 * @param {Object} props
 * @param {(task: Object) => void} props.onTaskCreated - Callback function called when a new task is created.
 */
function TaskForm({ onTaskCreated }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  /**
   * Handles form submission to create a new task.
   *
   * @param {React.FormEvent<HTMLFormElement>} e - The form submission event.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!title.trim()) return;

    const newTask = {
      title,
      description,
      completed: false,
    };

    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newTask),
      });

      if (!response.ok) {
        throw new Error('Chyba pri vytváraní úlohy');
      }

      const createdTask = await response.json();
      onTaskCreated(createdTask); // notify parent component about the new task
      setTitle('');
      setDescription('');
    } catch (err) {
      console.error("Chyba:", err);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: "1rem" }}>
      <div>
        <input
          type="text"
          placeholder="Názov úlohy"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
      </div>
      <div>
        <textarea
          placeholder="Popis (nepovinný)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </div>
      <button type="submit">Pridať úlohu</button>
    </form>
  );
}

export default TaskForm;

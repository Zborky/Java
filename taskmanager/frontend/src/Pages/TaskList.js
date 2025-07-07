import { useEffect, useState } from 'react';
import TaskForm from './TaskForm';
import './CSS/TaskList.css';

const API_URL = 'http://localhost:8080/api/tasks';

/**
 * TaskList component manages and displays a list of tasks.
 *
 * It loads tasks from the API, allows creating new tasks,
 * marking tasks as done, and deleting tasks.
 */
function TaskList() {
  const [tasks, setTasks] = useState([]);

  // Load tasks once when component mounts
  useEffect(() => {
    loadTasks();
  }, []);

  /**
   * Fetches all tasks from the backend API and updates state.
   */
  const loadTasks = async () => {
    try {
      const response = await fetch(API_URL);
      const data = await response.json();
      setTasks(data);
    } catch (err) {
      console.error('Chyba pri načítaní úloh:', err);
    }
  };

  /**
   * Adds a newly created task to the current list.
   *
   * @param {Object} newTask - The newly created task object.
   */
  const handleTaskCreated = (newTask) => {
    setTasks(prev => [...prev, newTask]);
  };

  /**
   * Deletes a task by its ID and updates the list.
   *
   * @param {number} id - The ID of the task to delete.
   */
  const handleDelete = async (id) => {
    try {
      await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
      });
      setTasks(prev => prev.filter(task => task.id !== id));
    } catch (error) {
      console.error('Chyba pri mazaní úlohy:', error);
    }
  };

  /**
   * Marks a task as completed by updating it on the backend
   * and updating the state accordingly.
   *
   * @param {number} id - The ID of the task to mark as done.
   */
  const handleMarkDone = async (id) => {
    try {
      const updatedTask = tasks.find(t => t.id === id);
      const response = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...updatedTask, completed: true })
      });

      if (!response.ok) throw new Error('Chyba pri označení ako hotovo');

      const updated = await response.json();
      setTasks(prev =>
        prev.map(task => (task.id === id ? updated : task))
      );
    } catch (error) {
      console.error('Chyba pri označovaní úlohy ako hotovej:', error);
    }
  };

  return (
    <div className="task-list-container">
      <h2>Úlohy</h2>
      <TaskForm onTaskCreated={handleTaskCreated} />
      <ul>
        {tasks.map(task => (
          <li key={task.id} className={`task-item ${task.completed ? 'completed' : ''}`}>
            <span>
              <strong>{task.title}</strong> - {task.completed ? 'Hotovo' : 'Otvorené'}
            </span>
            <div className="task-actions">
              {!task.completed && (
                <button onClick={() => handleMarkDone(task.id)}>Hotovo</button>
              )}
              <button className="delete" onClick={() => handleDelete(task.id)}>Vymazať</button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TaskList;

import React from 'react';
import TaskList from './Pages/TaskList';

function App() {
  return (
    <div style={{ maxWidth: '600px', margin: '0 auto', padding: '2rem' }}>
      <h1>Task Manager</h1>
      <TaskList />
    </div>
  );
}

export default App;

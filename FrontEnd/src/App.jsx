import React from "react";
import { QuizProvider } from "./context/quiz-context";
import Quiz from "./components/quiz";
import Login from "./components/loginPage/login"
function App() {
  return (

    
        <Login />




    // <QuizProvider>
      // <div className="app-container">
       // <h1>Quiz App</h1>
        // <Quiz />
     // </div>
    //</QuizProvider>
  );
}

export default App;
import React, { createContext, useState } from "react";

// Create a Context for the quiz
export const QuizContext = createContext();

// QuizProvider component wraps our app and provides quiz state and methods
export const QuizProvider = ({ children }) => {
  // Sample quiz questions stored as an array of objects
  const quizData = [
    {
      question: "What is the capital of France?",
      options: ["Berlin", "London", "Paris", "Rome"],
      answer: "Paris",
    },
    {
      question: "Which language is used for web apps?",
      options: ["PHP", "Python", "Javascript", "All"],
      answer: "All",
    },
    {
      question: "Who is the current CEO of Tesla?",
      options: ["Jeff Bezos", "Elon Musk", "Bill Gates", "Tony Stark"],
      answer: "Elon Musk",
    },
  ];

  // Initialize userAnswers as an array with a null value for each question
  const initialAnswers = quizData.map(() => null);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [userAnswers, setUserAnswers] = useState(initialAnswers);

  // Function to update the answer for a given question index
  const setAnswerForQuestion = (index, answer) => {
    setUserAnswers((prevAnswers) => {
      const newAnswers = [...prevAnswers];
      newAnswers[index] = answer;
      return newAnswers;
    });
  };

  // Reset function to restart the quiz
  const resetQuiz = () => {
    setCurrentQuestion(0);
    setUserAnswers(quizData.map(() => null));
  };

  return (
    <QuizContext.Provider
      value={{
        quizData,
        currentQuestion,
        setCurrentQuestion,
        userAnswers,
        setAnswerForQuestion,
        resetQuiz,
      }}
    >
      {children}
    </QuizContext.Provider>
  );
};
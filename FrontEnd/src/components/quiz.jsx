import React, { useContext } from "react";
import { QuizContext } from "../context/quiz-context";
import Question from "./question";
import Result from "./result";

const Quiz = () => {
  const { quizData, currentQuestion } = useContext(QuizContext);

  // If the user has finished all questions, show the results; otherwise, render the current question.
  if (currentQuestion >= quizData.length) {
    return <Result />;
  } else {
    return <Question />;
  }
};

export default Quiz;
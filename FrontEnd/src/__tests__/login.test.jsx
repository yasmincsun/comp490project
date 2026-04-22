import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "../components/loginPage/login";

describe("Login page", () => {
  test("renders login form and disables submit when empty", () => {
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    expect(screen.getByRole("button", { name: /login/i })).toBeDisabled();
    expect(screen.getByRole("button", { name: /sign up/i })).toBeEnabled();
    expect(screen.getByRole("button", { name: /submit/i })).toBeDisabled();
  });

  test("shows signup fields and validates invalid email", async () => {
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.click(screen.getByRole("button", { name: /sign up/i }));
    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText(/first name/i), { target: { value: "Jane" } });
    fireEvent.change(screen.getByLabelText(/last name/i), { target: { value: "Doe" } });
    fireEvent.change(screen.getByLabelText(/username/i), { target: { value: "janedoe" } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "bad-email" } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "strongpass123" } });

    const submitButton = screen.getByRole("button", { name: /submit/i });
    expect(submitButton).toBeEnabled();

    fireEvent.click(submitButton);
    expect(await screen.findByText(/invalid email address/i)).toBeInTheDocument();
  });
});

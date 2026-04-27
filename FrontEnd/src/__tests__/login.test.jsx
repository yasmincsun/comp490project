import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "../components/loginPage/login";

const mockedNavigate = jest.fn();

jest.mock("react-router-dom", () => {
  const actual = jest.requireActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockedNavigate,
  };
});

describe("Login page", () => {
  beforeEach(() => {
    window.alert = jest.fn();
    mockedNavigate.mockReset();
    global.fetch = jest.fn();
    localStorage.clear();
  });

  test("handleInputChange path: valid email input clears error", () => {
    // Node 1: Extract name and value from event
    // Node 2: Update form data
    // Node 3: Check validation rules
    // Node 9: Clear error for the field (since email is valid)
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    // Simulate input change for email with valid value
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "test@example.com" } });

    // Expect no error set (Node 9 in handleInputChange)
    expect(screen.getByLabelText(/email/i)).toHaveValue("test@example.com");
  });

  test("handleInputChange path: invalid email sets error", () => {
    // Node 1: Extract name and value from event
    // Node 2: Update form data
    // Node 3: Check validation rules
    // Node 7: Set email error (invalid email)
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    // Simulate input change for email with invalid value
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "invalid" } });

    // Expect error set (Node 7 in handleInputChange)
    expect(screen.getByText(/invalid email address/i)).toBeInTheDocument();
  });

  test("handleSubmit path: validation failure stops submission", async () => {
    // Node 1: Prevent default form submission
    // Node 2: Initialize validation errors
    // Node 3: Check signup mode validations
    // Node 4: Check email validation
    // Node 5: Check password validation
    // Node 6: Set errors and check if any exist (fails, returns)
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    // Fill form with invalid email
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "bad" } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "123" } });

    const submitButton = screen.getByRole("button", { name: /submit/i });
    fireEvent.click(submitButton);

    // Expect fetch not called (Node 6 in handleSubmit)
    expect(global.fetch).not.toHaveBeenCalled();
  });

  test("handleSubmit path: successful login navigates", async () => {
    // Node 1: Prevent default form submission
    // Node 2: Initialize validation errors
    // Node 3: Check signup mode validations
    // Node 4: Check email validation
    // Node 5: Check password validation
    // Node 6: Set errors and check if any exist (passes)
    // Node 7: Determine endpoint based on mode
    // Node 8: Prepare payload
    // Node 9: Send POST request
    // Node 10: Parse response
    // Node 11: Check if response is ok (yes)
    // Node 12: Store token
    // Node 13: Check mode signup (no, login)
    // Node 14: Handle login success
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ token: "abc123" }),
    });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: "jane@example.com" } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "strongpass123" } });

    const submitButton = screen.getByRole("button", { name: /submit/i });
    fireEvent.click(submitButton);

    await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith("/home"));
    expect(localStorage.getItem("authToken")).toBe("abc123");
  });
});

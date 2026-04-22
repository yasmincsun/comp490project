import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import ReviewPage from "../components/reviewPage/reviewPage";

describe("ReviewPage component", () => {
  beforeEach(() => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        text: () => Promise.resolve("")
      })
    );
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  test("renders write review view and search input", () => {
    render(
      <MemoryRouter>
        <ReviewPage />
      </MemoryRouter>
    );

    expect(screen.getByRole("button", { name: /write review/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /search reviews/i })).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/enter song title, artist, or album name/i)).toBeInTheDocument();
  });

  test("switches to Search Reviews tab and shows review search input", async () => {
    render(
      <MemoryRouter>
        <ReviewPage />
      </MemoryRouter>
    );

    fireEvent.click(screen.getByRole("button", { name: /search reviews/i }));
    expect(await screen.findByPlaceholderText(/search by song, artist, or album/i)).toBeInTheDocument();
  });
});

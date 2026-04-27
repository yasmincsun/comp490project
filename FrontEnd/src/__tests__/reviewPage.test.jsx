import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import ReviewPage from "../components/reviewPage/reviewPage";

describe("ReviewPage component", () => {
  beforeEach(() => {
    window.localStorage.setItem("authToken", "fake-token");
    global.fetch = jest.fn((url) => {
      if (url.includes("/api/v1/profile")) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve({ id: 1, color: 255 }),
        });
      }

      if (url.includes("/api/v1/spotify/search")) {
        return Promise.resolve({
          ok: true,
          json: () =>
            Promise.resolve({
              tracks: {
                items: [
                  {
                    id: "track-1",
                    name: "Test Song",
                    artists: ["Test Artist"],
                    album: { name: "Test Album", images: [{ url: "https://example.com/cover.jpg" }] },
                  },
                ],
              },
            }),
        });
      }

      if (url.includes("/api/v1/reviews/search")) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([]),
        });
      }

      return Promise.resolve({ ok: false, text: () => Promise.resolve("") });
    });
  });

  afterEach(() => {
    jest.resetAllMocks();
    window.localStorage.clear();
  });

  test("handleSearch path: empty query clears results", async () => {
    // Node 1: Check if search query is empty (yes)
    // Node 2: Clear search results
    render(
      <MemoryRouter>
        <ReviewPage />
      </MemoryRouter>
    );

    await waitFor(() => expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining("/api/v1/profile"), expect.anything()));

    // Trigger search with empty query
    fireEvent.click(screen.getByRole("button", { name: /^Search$/i }));

    // Expect no fetch for search (Node 1-2 in handleSearch)
    expect(global.fetch).toHaveBeenCalledTimes(1); // Only profile fetch
  });

  test("handleSearch path: successful track search sets results", async () => {    // Node 1: Check if search query is empty (no)
    // Node 3: Set loading state
    // Node 4: Clear error
    // Node 5: Get auth token
    // Node 6: Make fetch request
    // Node 7: Check if response not ok (no)
    // Node 9: Parse JSON data
    // Node 10: Process tracks
    // Node 13: Set search results
    // Node 15: Clear loading    render(
      <MemoryRouter>
        <ReviewPage />
      </MemoryRouter>
    );

    await waitFor(() => expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining("/api/v1/profile"), expect.anything()));

    fireEvent.change(screen.getByPlaceholderText(/enter song title, artist, or album name/i), {
      target: { value: "Test" },
    });
    fireEvent.click(screen.getByRole("button", { name: /^Search$/i }));

    await waitFor(() => expect(screen.getByText(/test song/i)).toBeInTheDocument());
    expect(screen.getByText(/test artist/i)).toBeInTheDocument();
  });

  test("handleReviewSearch path: empty query clears results", async () => {
    // Node 1: Check if review search query is empty (yes)
    // Node 2: Clear results and no results flag
    render(
      <MemoryRouter>
        <ReviewPage />
      </MemoryRouter>
    );

    await waitFor(() => expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining("/api/v1/profile"), expect.anything()));

    fireEvent.click(screen.getByRole("button", { name: /search reviews/i }));
    fireEvent.click(screen.getByRole("button", { name: /^Search$/i }));

    // Expect no fetch for review search (Node 1-2 in handleReviewSearch)
    expect(global.fetch).toHaveBeenCalledTimes(1); // Only profile fetch
  });

  test("handleReviewSearch path: successful search with no results", async () => {
    // Node 1: Check if review search query is empty (no)
    // Node 3: Set loading state
    // Node 4: Clear error and no results
    // Node 5: Get auth token
    // Node 6: Build URL
    // Node 7: Make fetch request
    // Node 8: Check if response not ok (no)
    // Node 10: Parse results
    // Node 11: Set results and check if empty (yes)
    // Node 13: Clear loading
    render(
      <MemoryRouter>
        <ReviewPage />
      </MemoryRouter>
    );

    await waitFor(() => expect(global.fetch).toHaveBeenCalledWith(expect.stringContaining("/api/v1/profile"), expect.anything()));

    fireEvent.click(screen.getByRole("button", { name: /search reviews/i }));
    fireEvent.change(screen.getByPlaceholderText(/search by song, artist, or album/i), {
      target: { value: "Nonexistent" },
    });
    fireEvent.click(screen.getByRole("button", { name: /^Search$/i }));

    await waitFor(() => expect(screen.getByText(/no reviews exist for this artist \/ song \/ album/i)).toBeInTheDocument());
  });
});

import "@testing-library/jest-dom";

// Polyfill for Node versions that do not expose TextEncoder/TextDecoder globally.
if (typeof TextEncoder === "undefined") {
  const { TextEncoder, TextDecoder } = require("util");
  global.TextEncoder = TextEncoder;
  global.TextDecoder = TextDecoder;
}

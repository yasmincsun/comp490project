import React, { useRef, useEffect, useState } from "react";
import * as maptilersdk from "@maptiler/sdk";
import { GeocodingControl } from "@maptiler/geocoding-control/maptilersdk";
import Box from "@mui/material/Box";

import "@maptiler/sdk/dist/maptiler-sdk.css";
import "./mapPage.css";

import Navbar from "./Navbar";
import configData from "./mapConfig";

const API_BASE_URL = "http://127.0.0.1:8080";

// Styles list
const baseMaps = {
  STREETS_V4: {
    img: "https://cloud.maptiler.com/static/img/maps/streets.png",
  },
  OPENSTREETMAP: {
    img: "https://cloud.maptiler.com/static/img/maps/openstreetmap.png",
  },
  HYBRID_V4: {
    img: "https://cloud.maptiler.com/static/img/maps/hybrid.png",
  },
};

class LayerSwitcherControl {
  constructor(options) {
    this._options = { ...options };
    this._container = document.createElement("div");
    this._container.classList.add(
      "maplibregl-ctrl",
      "maplibregl-ctrl-basemaps",
      "closed"
    );

    const dir = this._options.expandDirection || "right";
    if (dir === "top" || dir === "left") this._container.classList.add("reverse");
    if (dir === "top" || dir === "down") this._container.classList.add("column");
    else this._container.classList.add("row");

    this._container.addEventListener("mouseenter", () => {
      this._container.classList.remove("closed");
    });

    this._container.addEventListener("mouseleave", () => {
      this._container.classList.add("closed");
    });
  }

  onAdd(map) {
    this._map = map;

    const basemaps = this._options.basemaps;
    const initialId = this._options.initialBasemapId;

    Object.keys(basemaps).forEach((layerId) => {
      const base = basemaps[layerId];

      const img = document.createElement("img");
      img.src = base.img;
      img.classList.add("basemap");
      img.dataset.id = layerId;

      img.addEventListener("click", () => {
        const active = this._container.querySelector(".active");
        if (active) active.classList.remove("active");
        img.classList.add("active");

        this._map.setStyle(maptilersdk.MapStyle[layerId]);
      });

      if (layerId === initialId) img.classList.add("active");
      this._container.appendChild(img);
    });

    return this._container;
  }

  onRemove() {
    this._container.parentNode?.removeChild(this._container);
    delete this._map;
  }
}

export default function MapPage() {
  const mapContainer = useRef(null);
  const map = useRef(null);
  const eventMarkersRef = useRef([]);
  const keywordRef = useRef("");

  const center = { lng: -118.374107, lat: 34.1021 };
  const [zoom] = useState(4.2);
  const [keyword, setKeyword] = useState("");
  const [statusMsg, setStatusMsg] = useState("");

  keywordRef.current = keyword;
  maptilersdk.config.apiKey = configData.MAPTILER_API_KEY;

  function clearEventMarkers() {
    eventMarkersRef.current.forEach((marker) => marker.remove());
    eventMarkersRef.current = [];
  }

  function addEventsToMap(events) {
    if (!map.current) return;

    clearEventMarkers();

    const bounds = new maptilersdk.LngLatBounds();
    let markerCount = 0;

    for (const ev of events) {
      const venue = ev?._embedded?.venues?.[0];
      const lat = Number(venue?.location?.latitude);
      const lng = Number(venue?.location?.longitude);

      if (!Number.isFinite(lat) || !Number.isFinite(lng)) continue;

      const venueName = venue?.name || "Venue unavailable";
      const url = ev?.url || "#";

      const popupHtml = `
        <div style="max-width:220px;">
          <strong>${ev?.name || "Event"}</strong><br/>
          <span>${venueName}</span><br/>
          <a href="${url}" target="_blank" rel="noopener noreferrer">View event</a>
        </div>
      `;

      const marker = new maptilersdk.Marker()
        .setLngLat([lng, lat])
        .setPopup(new maptilersdk.Popup({ offset: 25 }).setHTML(popupHtml))
        .addTo(map.current);

      eventMarkersRef.current.push(marker);
      bounds.extend([lng, lat]);
      markerCount += 1;
    }

    if (markerCount === 1) {
      const marker = eventMarkersRef.current[0];
      const lngLat = marker.getLngLat();
      map.current.flyTo({
        center: [lngLat.lng, lngLat.lat],
        zoom: 10,
      });
    } else if (markerCount > 1) {
      map.current.fitBounds(bounds, {
        padding: 60,
        maxZoom: 10,
      });
    }
  }

  async function searchEventsFromBackend({ keyword: kw = "", lat = null, lng = null }) {
    setStatusMsg("Searching events...");

    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        throw new Error("You are not logged in.");
      }

      const params = new URLSearchParams();

      if (kw.trim()) {
        params.set("keyword", kw.trim());
      }

      if (lat !== null && lng !== null) {
        params.set("lat", String(lat));
        params.set("lng", String(lng));
      }

      const res = await fetch(`${API_BASE_URL}/api/v1/map/search?${params.toString()}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const rawText = await res.text();

      if (!res.ok) {
        throw new Error(`Backend returned ${res.status}: ${rawText}`);
      }

      const events = rawText ? JSON.parse(rawText) : [];
      const safeEvents = Array.isArray(events) ? events : [];

      setStatusMsg(
        `${safeEvents.length} events found${kw.trim() ? ` for "${kw.trim()}"` : ""}.`
      );

      addEventsToMap(safeEvents);
      return safeEvents;
    } catch (error) {
      console.error("Backend map search failed:", error);
      setStatusMsg(error.message || "Could not load events.");
      clearEventMarkers();
      return [];
    }
  }

  function runKeywordSearch(useMapCenter = false) {
    if (!keyword.trim()) {
      setStatusMsg("Enter a keyword to search.");
      return;
    }

    if (!useMapCenter) {
      searchEventsFromBackend({ keyword });
      return;
    }

    if (!map.current) return;

    const center = map.current.getCenter();

    searchEventsFromBackend({
      keyword,
      lat: center.lat,
      lng: center.lng,
    });
  }

  function getLocationAndEvents() {
    if (!navigator.geolocation) {
      setStatusMsg("Geolocation is not supported by this browser.");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      async (pos) => {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;

        if (map.current) {
          map.current.flyTo({ center: [lng, lat], zoom: 8 });
        }
      },
      (err) => {
        console.error(err);
        setStatusMsg("Location permission denied or unavailable.");
      }
    );
  }

  async function runAISearch() {
  setStatusMsg("AI searching concerts...");

  try {
    const token = localStorage.getItem("authToken");

    if (!token) {
      throw new Error("You are not logged in.");
    }

    if (!keyword.trim()) {
      throw new Error("Enter something to search.");
    }

    const params = new URLSearchParams({
      prompt: keyword.trim(),
    });

    const res = await fetch(`${API_BASE_URL}/api/v1/map/ai-search?${params.toString()}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const rawText = await res.text();

    if (!res.ok) {
      throw new Error(`Backend returned ${res.status}: ${rawText}`);
    }

    const events = rawText ? JSON.parse(rawText) : [];
    const safeEvents = Array.isArray(events) ? events : [];

    setStatusMsg(`${safeEvents.length} AI events found for "${keyword.trim()}".`);

    addEventsToMap(safeEvents);
    return safeEvents;
  } catch (error) {
    console.error("AI map search failed:", error);
    setStatusMsg(error.message || "Could not run AI search.");
    clearEventMarkers();
    return [];
  }
}

  useEffect(() => {
    if (map.current) return;

    const initialBasemapId = "STREETS_V4";

    map.current = new maptilersdk.Map({
      container: mapContainer.current,
      style: maptilersdk.MapStyle[initialBasemapId],
      center: [center.lng, center.lat],
      zoom,
      projectionControl: true,
    });

    const geocoder = new GeocodingControl();
    map.current.addControl(geocoder, "top-left");

    geocoder.on("result", async (e) => {
      const [lng, lat] = e.result.center;

      map.current.flyTo({ center: [lng, lat], zoom: 8 });

      if (keywordRef.current.trim()) {
        await searchEventsFromBackend({
          keyword: keywordRef.current,
          lat,
          lng,
        });
      }
    });

    map.current.addControl(
      new LayerSwitcherControl({
        basemaps: baseMaps,
        initialBasemapId,
        expandDirection: "right",
      }),
      "bottom-left"
    );

    map.current.on("load", () => {
      getLocationAndEvents();
    });

    return () => {
      clearEventMarkers();

      if (map.current) {
        map.current.remove();
        map.current = null;
      }
    };
  }, [center.lng, center.lat, zoom]);

  return (
    <Box sx={{ display: "flex" }}>
      <Navbar />
      <div className="container" style={{ position: "relative" }}>
        <div
          style={{
            position: "absolute",
            top: 12,
            right: 12,
            zIndex: 2,
            display: "flex",
            gap: 8,
            alignItems: "center",
            background: "rgba(255,255,255,0.92)",
            padding: 10,
            borderRadius: 10,
          }}
        >
          <input
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder='Keyword (e.g., "jazz", "drake")'
            style={{
              width: 240,
              padding: "8px 10px",
              borderRadius: 8,
              border: "1px solid #ccc",
              outline: "none",
            }}
          />

          <button
            onClick={() => runKeywordSearch(false)}
            style={{ padding: "8px 10px", borderRadius: 8, cursor: "pointer" }}
          >
            Search
          </button>

          <button
            onClick={runAISearch}
            style={{ padding: "8px 10px", borderRadius: 8, cursor: "pointer" }}
          >
            AI Search
          </button>

          <button
            onClick={() => runKeywordSearch(true)}
            style={{ padding: "8px 10px", borderRadius: 8, cursor: "pointer" }}
            title="Search using the current map center"
          >
            Use map center
          </button>
        </div>

        {statusMsg && (
          <div
            style={{
              position: "absolute",
              top: 62,
              right: 12,
              zIndex: 2,
              background: "rgba(255,255,255,0.92)",
              padding: "6px 10px",
              borderRadius: 10,
              maxWidth: 420,
              fontSize: 13,
              whiteSpace: "pre-wrap",
            }}
          >
            {statusMsg}
          </div>
        )}

        <div ref={mapContainer} id="map" className="map" />
      </div>
    </Box>
  );
}
import React, { useRef, useEffect, useState } from "react";`nimport { useNavigate } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import * as maptilersdk from "@maptiler/sdk";
import { GeocodingControl } from "@maptiler/geocoding-control/maptilersdk";
import Box from "@mui/material/Box";

import "@maptiler/sdk/dist/maptiler-sdk.css";
import "./mapPage.css";

import Navbar from "./Navbar";
import configData from "./mapConfig";

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

export default function MapPage() {`n  const navigate = useNavigate();
  const mapContainer = useRef(null);
  const map = useRef(null);
  const handlersRef = useRef({ click: null, enter: null, leave: null });
  const eventMarkersRef = useRef([]);
  const lastSearchCenterRef = useRef({ lat: 34.1021, lng: -118.374107 });
  const keywordRef = useRef("");

  const center = { lng: -118.374107, lat: 34.1021 };
  const [zoom] = useState(8.45);
  const [keyword, setKeyword] = useState("");
  const [statusMsg, setStatusMsg] = useState("");

  keywordRef.current = keyword;
  maptilersdk.config.apiKey = configData.MAPTILER_API_KEY;

  function clearEventMarkers() {
    eventMarkersRef.current.forEach((marker) => marker.remove());
    eventMarkersRef.current = [];
  }

  function addEventsToMap(json) {
    if (!map.current) return;

    clearEventMarkers();

    const events = json?._embedded?.events ?? [];

    for (const ev of events) {
      const venue = ev?._embedded?.venues?.[0];
      const lat = Number(venue?.location?.latitude);
      const lng = Number(venue?.location?.longitude);

      if (!Number.isFinite(lat) || !Number.isFinite(lng)) continue;

      const marker = new maptilersdk.Marker()
        .setLngLat([lng, lat])
        .setPopup(new maptilersdk.Popup({ offset: 25 }).setText(ev.name))
        .addTo(map.current);

      eventMarkersRef.current.push(marker);
    }
  }

  async function searchTicketmaster({ lat, lng, keyword: kw = "" }) {
    setStatusMsg("Searching events...");

    try {
      const params = new URLSearchParams({
        apikey: configData.TICKETMASTER_API_KEY,
        latlong: `${lat},${lng}`,
      });

      if (kw.trim()) {
        params.set("keyword", kw.trim());
      }

      const url = `https://app.ticketmaster.com/discovery/v2/events.json?${params.toString()}`;
      const res = await fetch(url);
      const json = await res.json();

      const events = json?._embedded?.events ?? [];
      setStatusMsg(
        `${events.length} events found${kw.trim() ? ` for "${kw.trim()}"` : ""}.`
      );

      addEventsToMap(json);
      return json;
    } catch (error) {
      console.error("Ticketmaster search failed:", error);
      setStatusMsg("Could not load events.");
    }
  }

  function runKeywordSearch(useMapCenter = false) {
    if (!map.current) return;

    const { lat, lng } = useMapCenter
      ? {
          lat: map.current.getCenter().lat,
          lng: map.current.getCenter().lng,
        }
      : lastSearchCenterRef.current;

    searchTicketmaster({ lat, lng, keyword });
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

        lastSearchCenterRef.current = { lat, lng };

        if (map.current) {
          map.current.flyTo({ center: [lng, lat], zoom: 10 });
        }

        await searchTicketmaster({ lat, lng, keyword: "" });
      },
      (err) => {
        console.error(err);
        setStatusMsg("Location permission denied or unavailable.");
      }
    );
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

      lastSearchCenterRef.current = { lat, lng };
      map.current.flyTo({ center: [lng, lat], zoom: 10 });

      await searchTicketmaster({
        lat,
        lng,
        keyword: keywordRef.current,
      });
    });

    map.current.addControl(
      new LayerSwitcherControl({
        basemaps: baseMaps,
        initialBasemapId,
        expandDirection: "right",
      }),
      "bottom-left"
    );

    const ensurePlacesLayerAndEvents = () => {
      if (!map.current) return;

      if (!map.current.getSource("places")) {
        map.current.addSource("places", {
          type: "geojson",
          data: {
            type: "FeatureCollection",
            features: [
              {
                type: "Feature",
                properties: {
                  description:
                    "<strong>Example place</strong><p>This is a popup.</p>",
                },
                geometry: {
                  type: "Point",
                  coordinates: [-118.374107, 34.1021],
                },
              },
            ],
          },
        });
      }

      if (!map.current.getLayer("places")) {
        map.current.addLayer({
          id: "places",
          type: "circle",
          source: "places",
          paint: {
            "circle-radius": 7,
            "circle-stroke-width": 2,
          },
        });
      }

      const prev = handlersRef.current;

      if (prev.click) map.current.off("click", "places", prev.click);
      if (prev.enter) map.current.off("mouseenter", "places", prev.enter);
      if (prev.leave) map.current.off("mouseleave", "places", prev.leave);

      const onClick = (e) => {
        const coordinates = e.features[0].geometry.coordinates.slice();
        const description = e.features[0].properties.description;

        while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
          coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
        }

        new maptilersdk.Popup()
          .setLngLat(coordinates)
          .setHTML(description)
          .addTo(map.current);
      };

      const onEnter = () => {
        map.current.getCanvas().style.cursor = "pointer";
      };

      const onLeave = () => {
        map.current.getCanvas().style.cursor = "";
      };

      map.current.on("click", "places", onClick);
      map.current.on("mouseenter", "places", onEnter);
      map.current.on("mouseleave", "places", onLeave);

      handlersRef.current = {
        click: onClick,
        enter: onEnter,
        leave: onLeave,
      };
    };

    map.current.on("load", () => {
      ensurePlacesLayerAndEvents();
      getLocationAndEvents();
    });

    map.current.on("style.load", ensurePlacesLayerAndEvents);

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
      <div className="container" style={{ position: "relative" }}>`n        <button onClick={() => navigate(-1)} style={{ position: "absolute", top: 12, left: 12, zIndex: 2, padding: "8px 12px", borderRadius: 8, cursor: "pointer", fontFamily: "Montserrat, sans-serif" }}>Back</button>
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
            onKeyDown={(e) => {
              if (e.key === "Enter") runKeywordSearch(false);
            }}
            placeholder='Keyword (e.g., "jazz", "lakers")'
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

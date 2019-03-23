package edu.skku.finalproject.studyhelper.dto;

public class MapJsonData {
    public ResultClass[] results = null;

    public ResultClass[] getResults() {
        return results;
    }

    public void setResults(ResultClass[] results) {
        this.results = results;
    }

    public class ResultClass {
        GeometryClass geometry = null;
        String icon = "";
        public String name = "";
        public String place_id = "";
        String vicinity = "";

        public GeometryClass getGeometry() {
            return geometry;
        }

        public void setGeometry(GeometryClass geometry) {
            this.geometry = geometry;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }

        public class GeometryClass {
            LocationClass location = null;

            public LocationClass getLocation() {
                return location;
            }

            public void setLocation(LocationClass location) {
                this.location = location;
            }

            public class LocationClass {
                double lat = 0.0;
                double lng = 0.0;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }
            }
        }
    }
}

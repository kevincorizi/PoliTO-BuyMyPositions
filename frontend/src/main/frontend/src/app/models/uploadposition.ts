import {JsonObject, JsonProperty} from 'json2typescript';

@JsonObject
export class UploadPosition {
    @JsonProperty("latitude", Number)
    latitude: Number = undefined;

    @JsonProperty("longitude", Number)
    longitude: Number = undefined;

    @JsonProperty("timestamp", Number)
    timestamp: Number = undefined;
}
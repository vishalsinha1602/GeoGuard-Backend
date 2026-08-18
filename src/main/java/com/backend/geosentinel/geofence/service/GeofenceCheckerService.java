package com.backend.geosentinel.geofence.service;

import com.backend.geosentinel.devices.entity.Device;

public interface GeofenceCheckerService {

    void checkGeofences(Device device);

}
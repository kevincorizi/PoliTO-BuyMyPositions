print("RUNNING INIT SCRIPT");

conn = new Mongo();
db = conn.getDB("polito_ai_lab3");
db.positions.drop();

db.positions.createIndex()

db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000001]},
    username: "max",
    timestamp: 1527094035200
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000002]},
    username: "max",
    timestamp: 1527094035202
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000003]},
    username: "max",
    timestamp: 1527094035204
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000004]},
    username: "max",
    timestamp: 1527094035206
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000005]},
    username: "max",
    timestamp: 1527094035208
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000006]},
    username: "max",
    timestamp: 1527094035210
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000007]},
    username: "max",
    timestamp: 1527094035212
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [-13.27, 41.77000000007]},
    username: "max",
    timestamp: 1527094035212
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [16.77119549153708, 41.11812225212022]},
    username: "max",
    timestamp: 1527394035212
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [16.286692, 41.325278]},
    username: "max",
    timestamp: 1527694035212
})
db.positions.insert({
    geoPoint: {type: "Point", coordinates: [7.662207, 45.062097]},
    username: "max",
    timestamp: 1527994035212
})


print("INIT SCRIPT FINISHED");

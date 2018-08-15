## Valid positions

- `cron-ord1.json` contains positions in Milan and Geneve
- `cron-ord2.json` contains positinos in Turin and Rome
- `cronB-ord1.json` contains positions in Geneve
- `cronB-ord2.json` contains positions in Milan and Turin
- `cronC-ord1.json` contains positions in Rome

- `last1.json` contains positions in London
- `last2.json` contains positions in Greece 

#### Possible uploads
Every X sequences `cronX-ordN.json` of files can be uploaded in the order specified by N.
The `lastY.json` files can be upload at the end of each previous sequence.

For example, it is possible to upload:
```
cronB-ord1.json
cronB-ord2.json
last1.json
last2.json
```

```
cron-ord1.json
cron-ord2.json
last2.json
```

```
cronC-ord1.json
last1.json
last2.json
```

And more other combinations.

## Invalid positions
- `future.json` contains a positions with a timestamp in the future
- `too-fast.json` contains a sequence of positions that not respect the speed constraint
- `invalid-partX.json` files can be used to simulate an upload of an archive that violates the speed constraint due to the presence of a another archive 
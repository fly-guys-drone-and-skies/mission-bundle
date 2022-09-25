package edu.rit.se.sars.communication.network.entity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry of known network entities in swarm
 * @param <T> Network entity type used for swarm
 */
public class NetworkEntityRegistry<T extends NetworkEntity> {

    private final T self;

    private final Map<UUID, T> entities = new HashMap<>();
    private final Map<NetworkEntityType, Set<T>> entityTypeMap = new HashMap<>();

    /**
     * @param self Entity representation of self to be broadcasted to other entities
     */
    public NetworkEntityRegistry(T self) {
        this.self = self;

        this.addEntity(this.self);
    }

    public T getSelf() {
        return this.self;
    }

    /**
     * Add entity to registry
     * @param entity Entity to add
     */
    public void addEntity(T entity) {
        entities.put(entity.getUuid(), entity);

        entityTypeMap.putIfAbsent(entity.getType(), new HashSet<>());
        entityTypeMap.get(entity.getType()).add(entity);
    }

    /**
     * Get entity by UUID
     * @param entityUUID Entity UUID
     * @return Entity with specified UUID
     */
    public Optional<T> getEntity(UUID entityUUID) {
        return Optional.ofNullable(
            entities.get(entityUUID)
        );
    }

    /**
     * @return All registered external entities
     */
    public Set<T> getOtherEntities() {
        return this.entities.values().stream()
            .filter(e -> !e.equals(this.self))
            .collect(Collectors.toSet());
    }

    /**
     * Get all external entities of type
     * @param type Entity type
     * @return All known external entities of specified type
     */
    public Set<T> getOtherEntitiesOfType(NetworkEntityType type) {
        return this.getAllEntitiesOfType(type).stream()
            .filter(e -> !e.equals(this.self))
            .collect(Collectors.toSet());
    }

    /**
     * Get all entities of type, including self if applicable
     * @param type Entity type
     * @return All known entities of specified type
     */
    public Set<T> getAllEntitiesOfType(NetworkEntityType type) {
        return this.entityTypeMap.getOrDefault(type, new HashSet<>());
    }

    /**
     * @return Map of all external entities by UUID
     */
    public Map<UUID, T> getEntityMap() {
        return this.entities;
    }
}

package net.script.data;


import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public abstract class CachingRepository<T> {
    private final CrudRepository<T, Long> repo;
    private Iterable<T> cachedData;

    protected CachingRepository(CrudRepository<T, Long> repository) {
        this.repo = repository;
    }

    public abstract Class<T> getItemClass();

    public <S extends T> S save(S s) {
        return repo.save(s);
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> iterable) {
        return repo.saveAll(iterable);
    }

    public Optional<T> findById(Long integer) {
        return repo.findById(integer);
    }

    public boolean existsById(Long integer) {
        return repo.existsById(integer);
    }

    public Iterable<T> findAll() {
        if (this.cachedData == null) {
            this.cachedData = repo.findAll();
        }
        return this.cachedData;
    }

    public Iterable<T> findAllById(Iterable<Long> iterable) {
        return repo.findAllById(iterable);
    }

    public long count() {
        return repo.count();
    }

    public void deleteById(Long integer) {
        repo.deleteById(integer);
    }

    public void delete(T t) {
        repo.delete(t);
    }

    public void deleteAll(Iterable<? extends T> iterable) {
        repo.deleteAll(iterable);
    }

    public void deleteAll() {
        repo.deleteAll();
    }
}

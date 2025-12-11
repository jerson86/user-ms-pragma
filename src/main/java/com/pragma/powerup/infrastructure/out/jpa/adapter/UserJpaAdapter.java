package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository objectRepository;
    private final IUserEntityMapper objectEntityMapper;


    @Override
    public UserModel saveUser(UserModel userModel) {
        UserEntity objectEntity = objectRepository.save(objectEntityMapper.toEntity(userModel));
        return objectEntityMapper.toObjectModel(objectEntity);
    }

    @Override
    public UserModel findByCorreo(String correo) {
        Optional<UserEntity> userEntity = objectRepository.findByCorreo(correo);
        return userEntity.map(objectEntityMapper::toObjectModel).orElse(null);
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return objectRepository.existsByDocumento(documento);
    }

    @Override
    public UserModel findById(Long id) {
        Optional<UserEntity> entityOptional = objectRepository.findById(id);

        if (entityOptional.isEmpty()) {
            log.warn("Usuario con ID {} no encontrado en la base de datos.", id);
            return null;
        }

        log.info("Usuario con ID {} encontrado y mapeado.", id);
        return objectEntityMapper.toObjectModel(entityOptional.get());
    }
}
package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
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
        return objectRepository.findById(id).map(objectEntityMapper::toObjectModel).orElse(null);
    }
}
package com.foodie.application.service;

import com.foodie.application.domain.Role;
import com.foodie.application.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Integer id) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Transactional
    public Role findOrCreateRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
    }

    @Transactional
    public Role addRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);

        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRoleName(Integer id, String roleName) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        role.setName(roleName);

        return role;
    }

    @Transactional
    public void deleteRole(Integer roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new EntityNotFoundException("Role not found with id: " + roleId);
        }
        roleRepository.deleteById(roleId);
    }


}

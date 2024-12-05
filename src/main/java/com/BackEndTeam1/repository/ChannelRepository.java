package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Integer> {

}

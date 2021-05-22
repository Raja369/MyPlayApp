package org.myplay.repo;

import java.util.List;

import org.myplay.models.FavouritePlaylist;
import org.myplay.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteRepo extends JpaRepository<FavouritePlaylist,Integer> {

	List<FavouritePlaylist> findByUser(User user);

}

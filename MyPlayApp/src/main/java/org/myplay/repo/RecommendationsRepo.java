package org.myplay.repo;

import java.util.List;

import org.myplay.models.Recommendations;
import org.myplay.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationsRepo extends JpaRepository<Recommendations,Integer> {

	List<Recommendations> findAllByUser(User user);

}

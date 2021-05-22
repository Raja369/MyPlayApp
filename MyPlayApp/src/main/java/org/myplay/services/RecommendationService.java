package org.myplay.services;

import java.util.List;

import org.myplay.models.Recommendations;
import org.myplay.models.User;
import org.myplay.repo.RecommendationsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

	@Autowired
	private RecommendationsRepo recomRepo;

	public Recommendations saveRecommendation(Recommendations track) {
		List<Recommendations> tracks = recomRepo.findAllByUser(track.getUser());
		for (int i = 0; i < tracks.size(); i++) {
			if (tracks.get(i).getArtist().equals(track.getArtist())
					&& (track.getTrackName() == null || tracks.get(i).getTrackName().equals(track.getTrackName()))) {
				return recomRepo.save(tracks.get(i));
			}
		}
		return recomRepo.save(track);
	}

	public List<Recommendations> getAllRecommendations(User user) {
		return recomRepo.findAllByUser(user);
	}

	public boolean deleteRecommendation(int id) {
		Recommendations recom = recomRepo.findById(id).orElse(null);
		if (recom != null) {
			recomRepo.deleteById(id);
			return true;
		}
		return false;
	}

	public boolean deleteRecommendation(Recommendations track) {
		List<Recommendations> tracks = recomRepo.findAllByUser(track.getUser());
		for (int i = 0; i < tracks.size(); i++) {
			if (tracks.get(i).getArtist().equals(track.getArtist())
					&& (track.getTrackName() == null || tracks.get(i).getTrackName().equals(track.getTrackName()))) {
				recomRepo.delete(tracks.get(i));
				return true;
			}
		}
		return false;
	}

}

package org.myplay.services;

import java.util.List;

import org.myplay.models.FavouritePlaylist;
import org.myplay.models.User;
import org.myplay.repo.FavouriteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavouriteService {
	@Autowired
	private FavouriteRepo favRepo;

	public FavouritePlaylist saveFavourite(FavouritePlaylist track) {
		List<FavouritePlaylist> tracks = getAllFavourites(track.getUser());
		for (int i = 0; i < tracks.size(); i++) {
			if (tracks.get(i).getArtist().equals(track.getArtist())
					&& (track.getTrackName() == null || tracks.get(i).getTrackName().equals(track.getTrackName()))) {
				return favRepo.save(tracks.get(i));
			}
		}
		return favRepo.save(track);
	}

	public List<FavouritePlaylist> getAllFavourites(User user) {
		return favRepo.findByUser(user);
	}

	public boolean deleteFavourite(int id) {
		FavouritePlaylist track = favRepo.findById(id).orElse(null);
		if (track != null) {
			favRepo.deleteById(id);
			return true;
		}
		return false;
	}

	public boolean deleteFavourite(FavouritePlaylist track) {
		List<FavouritePlaylist> tracks = getAllFavourites(track.getUser());
		for (int i = 0; i < tracks.size(); i++) {
			if (tracks.get(i).getArtist().equals(track.getArtist())
					&& (track.getTrackName() == null || tracks.get(i).getTrackName().equals(track.getTrackName()))) {
				favRepo.delete(tracks.get(i));
				return true;
			}
		}
		return false;
	}
}

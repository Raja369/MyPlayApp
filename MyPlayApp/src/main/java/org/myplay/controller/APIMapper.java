package org.myplay.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.myplay.configure.UserAuth;
import org.myplay.models.FavouritePlaylist;
import org.myplay.models.Recommendations;
import org.myplay.models.User;
import org.myplay.services.FavouriteService;
import org.myplay.services.RecommendationService;
import org.myplay.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class APIMapper {
	@Autowired
	private UserService userService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private RecommendationService recomService;

	@Autowired
	private FavouriteService favService;

	ObjectMapper objectMap = new ObjectMapper();

	@RequestMapping(value = "/RegisterUser", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> registerNewUser(@RequestParam(required = true, value = "profile") MultipartFile file,
			@RequestParam(required = true, value = "jsondata") String jsondata) throws IOException {
		HashMap<String, String> response = new HashMap();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsondata);

		} catch (JSONException err) {
			response.put("Error", "Cannot serve request");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		Optional<User> prevUser = userService.findUserByEmail(jsonObject.getString("email"));
		if (jsonObject.getString("email") == null || jsonObject.getString("name") == null
				|| jsonObject.getString("password") == null) {
			response.put("Error", "Invalid Parameters");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		if (prevUser.isPresent()) {
			response.put("Error", "Email already registered");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			User user = new User();
			String temp[] = file.getOriginalFilename().split("\\.");
			String tempFileName = System.currentTimeMillis() + "." + (temp[temp.length - 1]);

			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("img/", tempFileName)));
			stream.write(file.getBytes());
			stream.close();
			user.setEmail(jsonObject.getString("email"));
			user.setName(jsonObject.getString("name"));
			user.setPassword(jsonObject.getString("password"));
			user.setImg(tempFileName);
			userService.createUser(user);
			response.put("UserCreated", "New User created successfully");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@GetMapping("/GetUser")
	public ResponseEntity getUserDeatils() {
		HashMap<String, String> responseMap = new HashMap<>();

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			User loggedinUser = ((UserAuth) principal).getUser();
			responseMap.put("UserName", loggedinUser.getName());
			responseMap.put("Email", loggedinUser.getEmail());
			responseMap.put("img", loggedinUser.getImg());

		} else {
			responseMap.put("Error", "Cannot get user details");
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);

	}

	@PostMapping(value = "/updateUser", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Map<String, String> updateUser(@RequestParam("oldPassword") String oldPass,
			@RequestParam("newPassword") String newpass) {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			User loggedinUser = ((UserAuth) principal).getUser();
			if (bCryptPasswordEncoder.matches(oldPass, loggedinUser.getPassword().replace("{bcrypt}", ""))) {

				loggedinUser.setPassword(newpass);
				userService.updateUser(loggedinUser);
				responseMap.put("Updated", "Profile updated");
			} else {
				responseMap.put("Error", "Old password is wrong");
			}
		} else {
			responseMap.put("Error", "Cannot update profile");
		}

		return responseMap;
	}

	@PostMapping(value = "/AddToRecommend", produces = { MediaType.APPLICATION_JSON_VALUE })
	// @RequestMapping(value="/recommendation",method=
	// RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity createRecommendation(@ModelAttribute Recommendations track) {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserAuth) principal).getUsername();
			Optional<User> existingUser = userService.findUserByEmail(username);
			track.setUser(existingUser.get());
			recomService.saveRecommendation(track);
			responseMap.put("Added", "Recommendation added");
		} else {
			responseMap.put("Error", "Recommendation cannot be added");
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@DeleteMapping("/deleteRecommendation/{id}")
	public ResponseEntity deleteRecommendation(@PathVariable int id) {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			recomService.deleteRecommendation(id);
			responseMap.put("Deleted", "Recommendation deleted");
		} else {
			responseMap.put("Error", "Recommendation cannot be deleted");
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping("/getAllRecommendations")
	public ResponseEntity getRecommendations() {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserAuth) principal).getUsername();
			Optional<User> existingUser = userService.findUserByEmail(username);
			List<Recommendations> recomTracks = recomService.getAllRecommendations(existingUser.get());
			HashMap<String, Map<Integer, Map<String, String>>> returnMap = new HashMap();
			HashMap<Integer, Map<String, String>> recomMap = new HashMap<>();
			for (int i = 0; i < recomTracks.size(); i++) {
				HashMap<String, String> tempMap = new HashMap();
				tempMap.put("trackName", recomTracks.get(i).getTrackName());
				tempMap.put("id", String.valueOf(recomTracks.get(i).getId()));
				tempMap.put("artistName", recomTracks.get(i).getArtist());
				tempMap.put("img", recomTracks.get(i).getImg());
				recomMap.put(i, tempMap);
			}
			returnMap.put("TRACKS", recomMap);
			return new ResponseEntity<>(returnMap, HttpStatus.OK);
		} else {
			responseMap.put("Error", "Cannot get recommendations");
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		}

	}

	@DeleteMapping(value = "/deleteRecommendation", produces = { MediaType.APPLICATION_JSON_VALUE })
	// @RequestMapping(value="/recommendation",method=
	// RequestMethod.DELETE,consumes=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> deleteRecommendationTrack(@ModelAttribute Recommendations track) {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserAuth) principal).getUsername();
			Optional<User> existingUser = userService.findUserByEmail(username);
			track.setUser(existingUser.get());
			if (recomService.deleteRecommendation(track)) {
				responseMap.put("Deleted", "Recommendation removed");
			} else {
				responseMap.put("Error", "Recommendation not found");
			}

		} else {
			responseMap.put("Error", "Invalid request");
		}
		return responseMap;
	}

	@PostMapping(value = "/AddToFavourite", produces = { MediaType.APPLICATION_JSON_VALUE })
	// @RequestMapping(value="/favourite",method=
	// RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity createFavourite(@ModelAttribute FavouritePlaylist track) {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserAuth) principal).getUsername();
			Optional<User> existingUser = userService.findUserByEmail(username);
			track.setUser(existingUser.get());
			favService.saveFavourite(track);
			responseMap.put("Added", "Favourites added");
		} else {
			responseMap.put("Error", "Favourites cannot be added");
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping("/getAllFavourites")
	public ResponseEntity getFavourites() {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			HashMap<String, Map<Integer, Map<String, String>>> returnMap = new HashMap();
			HashMap<Integer, Map<String, String>> trackMap = new HashMap<>();
			String username = ((UserAuth) principal).getUsername();
			Optional<User> existingUser = userService.findUserByEmail(username);
			List<FavouritePlaylist> favTracks = favService.getAllFavourites(existingUser.get());

			for (int i = 0; i < favTracks.size(); i++) {
				HashMap<String, String> tempMap = new HashMap();
				tempMap.put("trackName", favTracks.get(i).getTrackName());
				tempMap.put("id", String.valueOf(favTracks.get(i).getId()));
				tempMap.put("artistName", favTracks.get(i).getArtist());
				tempMap.put("img", favTracks.get(i).getImg());
				trackMap.put(i, tempMap);
			}
			returnMap.put("TRACKS", trackMap);
			return new ResponseEntity<>(returnMap, HttpStatus.OK);

		} else {
			responseMap.put("Error", "Cannot get favourites");
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		}
	}

	@DeleteMapping(value = "/deleteFavourite", produces = { MediaType.APPLICATION_JSON_VALUE })
	// @RequestMapping(value="/favourite",method=
	// RequestMethod.DELETE,consumes=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> deleteFavouriteTrack(@ModelAttribute FavouritePlaylist track) {
		HashMap<String, String> responseMap = new HashMap<>();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserAuth) principal).getUsername();
			Optional<User> existingUser = userService.findUserByEmail(username);
			track.setUser(existingUser.get());
			if (favService.deleteFavourite(track)) {
				responseMap.put("Deleted", "Favourite removed");
			} else {
				responseMap.put("Error", "Favourire not found");
			}

		} else {
			responseMap.put("Error", "Invalid request");
		}
		return responseMap;
	}

	@DeleteMapping("/deleteFavourite/{id}")
	public ResponseEntity deleteFavourite(@PathVariable int id) {
		HashMap<String, String> responseMap = new HashMap<>();
		if (favService.deleteFavourite(id)) {
			responseMap.put("Deleted", "Favourite item deleted");
		} else {
			responseMap.put("Failed", "Favourite item not found");
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

}

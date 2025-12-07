package com.sengar.journal.controller;

import com.sengar.journal.entity.JournalEntry;
import com.sengar.journal.entity.User;
import com.sengar.journal.service.JournalEntryService;
import com.sengar.journal.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntries() {
        try {
            List<JournalEntry> list = journalEntryService.getAllJournals();
            if(!list.isEmpty()) {
                return new ResponseEntity<>(list, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No journal entries found.", HttpStatus.NO_CONTENT);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getJournalById(@PathVariable ObjectId id) {
        try {
            Optional<JournalEntry> entry = journalEntryService.findById(id);
            if(entry.isPresent()) {
                return new ResponseEntity<>(entry, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No journal entry found.", HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String username) {
        try {
            Optional<User> user = userService.findByUsername(username);
            if(user.isPresent()) {
                List<JournalEntry> list = user.get().getJournalEntries();
                if(!list.isEmpty()) {
                    return new ResponseEntity<>(list, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("No journal entry yet for this user.", HttpStatus.NO_CONTENT);
                }
            } else {
                return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> createEntry(@PathVariable String username, @RequestBody JournalEntry entry) {
        try {
            Optional<User> user = userService.findByUsername(username);
            if(user.isPresent()) {
                journalEntryService.saveEntry(user.get(), entry);
                return new ResponseEntity<>("Journal entry saved", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{username}/id/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String username, @PathVariable ObjectId id) {
        try {
            Optional<User> user = userService.findByUsername(username);
            if(user.isPresent()) {
                Optional<JournalEntry> entry = journalEntryService.findById(id);
                if(entry.isPresent()) {
                    journalEntryService.deleteById(entry.get().getId(), user.get());
                    return new ResponseEntity<>("Entry deleted", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Journal with ID: " + id.toString() + " does not exist.", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{username}/id/{id}")
    public ResponseEntity<?> updateById(@PathVariable String username, @RequestBody JournalEntry updatedEntry, @PathVariable ObjectId id) {
        try {
            Optional<User> user = userService.findByUsername(username);
            if(user.isPresent()) {
                Optional<JournalEntry> entry = journalEntryService.findById(id);
                if(entry.isPresent()) {
                    journalEntryService.updateById(id, user.get(), updatedEntry);
                    return new ResponseEntity<>(entry, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Journal with ID: " + id.toString() + " does not exist.", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

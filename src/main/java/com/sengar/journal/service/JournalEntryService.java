package com.sengar.journal.service;

import com.sengar.journal.entity.JournalEntry;
import com.sengar.journal.entity.User;
import com.sengar.journal.repository.JournalEntryRepository;
import com.sengar.journal.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public List<JournalEntry> getAllJournals() { return journalEntryRepository.findAll(); }

    public Optional<JournalEntry> findById(ObjectId id) { return journalEntryRepository.findById(id); }

    private void saveEntry(JournalEntry entry) {
        entry.setTimestamp(LocalDateTime.now());
        journalEntryRepository.save(entry);
    }

    @Transactional
    public void saveEntry(User user, JournalEntry entry) {
        try {
            // There is a parent child relationship between User and Journal Collections.
            // Any Entry to Journal collection should also reflect in User.
            // And these steps should be Atomic and Isolated for Asynchronous calls.
            // Hence, making this method @Transactional.
            // If any line of the code block fails, it rolls back the executed lines.

            entry.setTimestamp(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(entry);
            user.getJournalEntries().add(saved);
            userService.saveEntry(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("An error occurred while saving journal entry. ", e);
        }
    }

    public void deleteById(ObjectId id, User user) {
        // Cascading delete does not happen in MongoDB.
        // The User will still hold a null reference until it gets updated.
        // To maintain Consistency we are manually deleting the journal entry from the provided user.

        user.getJournalEntries().removeIf(item -> item.getId().equals(id));
        journalEntryRepository.deleteById(id);
        userService.saveEntry(user);
    }

    public void updateById(ObjectId id, User user, JournalEntry entry) {
        Optional<JournalEntry> old = findById(id);
        if(old.isPresent()) {
            old.get().setTitle(entry.getTitle());
            old.get().setContent(entry.getContent());
            saveEntry(old.get());
        }
    }

}

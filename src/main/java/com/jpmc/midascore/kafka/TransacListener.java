package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.TransacRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Users;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransacListener {

    @Value("${midas.kafka.topic}")
    private String topic;

    @Value("${incentive.api.url}")
    private String incentiveUrl;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(
            topics = "${midas.kafka.topic}",
            groupId = "midas-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(Transaction transaction) {

        Optional<Users> optionalSender = usersRepository.findById(transaction.getSenderId());
        Optional<Users> optionalRecipient = usersRepository.findById(transaction.getRecipientId());

        if (optionalSender.isEmpty() || optionalRecipient.isEmpty()) return;

        Users sender = optionalSender.get();
        Users recipient = optionalRecipient.get();

        // Check balance
        if (sender.getBalance() < transaction.getAmount()) return;

        // Call incentive service
        Incentive incentive = restTemplate.postForObject(
                incentiveUrl,
                transaction,
                Incentive.class
        );

        float incentiveAmount = (incentive != null) ? (float) incentive.getAmount() : 0;

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Save updated users
        usersRepository.save(sender);
        usersRepository.save(recipient);

        // Save transaction
        TransacRecord record = new TransacRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(transaction.getAmount());
        record.setIncentive(incentiveAmount);
        transactionRecordRepository.save(record);
    }
}

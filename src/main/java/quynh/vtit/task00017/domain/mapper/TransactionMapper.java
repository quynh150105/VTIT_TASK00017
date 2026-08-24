package quynh.vtit.task00017.domain.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import quynh.vtit.task00017.domain.dto.request.CreateTransactionRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateTransactionRequest;
import quynh.vtit.task00017.domain.dto.response.TransactionResponse;
import quynh.vtit.task00017.domain.dto.response.TransactionSummaryResponse;
import quynh.vtit.task00017.domain.entity.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "transferWallet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(CreateTransactionRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "walletName", source = "wallet.name")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "transferWalletId", source = "transferWallet.id")
    @Mapping(target = "transferWalletName", source = "transferWallet.name")
    TransactionResponse toResponse(Transaction transaction);

    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "transferWalletId", source = "transferWallet.id")
    TransactionSummaryResponse toSummaryResponse(Transaction transaction);

    List<TransactionSummaryResponse> toSummaryResponses(List<Transaction> transactions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "transferWallet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(UpdateTransactionRequest request, @MappingTarget Transaction transaction);
}

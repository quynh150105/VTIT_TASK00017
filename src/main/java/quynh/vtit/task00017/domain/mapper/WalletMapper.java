package quynh.vtit.task00017.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateWalletRequest;
import quynh.vtit.task00017.domain.dto.response.WalletResponse;
import quynh.vtit.task00017.domain.entity.Wallet;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "defaultWallet", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Wallet toWallet(CreateWalletRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "walletStatus", source = "status")
    WalletResponse toWalletResponse(Wallet wallet);

    List<WalletResponse> toListWalletResponse(List<Wallet> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "defaultWallet", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(UpdateWalletRequest request, @MappingTarget Wallet target);
}

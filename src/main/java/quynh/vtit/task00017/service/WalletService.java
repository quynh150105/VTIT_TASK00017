package quynh.vtit.task00017.service;

import java.util.List;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateWalletRequest;
import quynh.vtit.task00017.domain.dto.response.WalletResponse;

public interface WalletService {

    List<WalletResponse> getWallets();

    WalletResponse createWallet(CreateWalletRequest request);

    WalletResponse updateWallet(Long id, UpdateWalletRequest request);

    void deleteWallet(Long id);
}

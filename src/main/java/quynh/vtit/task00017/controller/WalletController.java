package quynh.vtit.task00017.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.RestApiV1;
import quynh.vtit.task00017.base.constant.UrlConstant;
import quynh.vtit.task00017.domain.dto.request.CreateWalletRequest;
import quynh.vtit.task00017.domain.dto.request.UpdateWalletRequest;
import quynh.vtit.task00017.domain.dto.response.WalletResponse;
import quynh.vtit.task00017.service.WalletService;

@RestApiV1
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping(UrlConstant.Wallet.GET_ALL)
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getWallets() {
        return ResponseEntity.ok(ApiResponse.ok("Get wallets successfully", walletService.getWallets()));
    }

    @PostMapping(UrlConstant.Wallet.CREATE)
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(
            @Valid @RequestBody CreateWalletRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Create wallet successfully", walletService.createWallet(request)));
    }

    @PutMapping(UrlConstant.Wallet.UPDATE)
    public ResponseEntity<ApiResponse<WalletResponse>> updateWallet(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWalletRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Update wallet successfully", walletService.updateWallet(id, request)));
    }

    @DeleteMapping(UrlConstant.Wallet.DELETE)
    public ResponseEntity<ApiResponse<Void>> deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
        return ResponseEntity.ok(ApiResponse.ok("Delete wallet successfully", null));
    }
}

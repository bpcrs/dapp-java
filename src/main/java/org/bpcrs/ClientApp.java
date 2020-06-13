/*
SPDX-License-Identifier: Apache-2.0
*/

package org.bpcrs;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.*;

public class ClientApp {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(String[] args) throws Exception {
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get(System.getProperty("network.config.path"));

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "admin").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("fabcar");

			byte[] result;
//
			result = contract.evaluateTransaction("queryAllCars");
			System.out.println(new String(result));
//
//			contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey", "Mary");
////
//			result = contract.evaluateTransaction("queryCar", "CAR10");
//			System.out.println(new String(result));
////
//			contract.submitTransaction("changeCarOwner", "CAR10", "HungPT");
//			result = contract.evaluateTransaction("queryCar", "CAR10");
//			System.out.println(new String(result));
		}
	}

}

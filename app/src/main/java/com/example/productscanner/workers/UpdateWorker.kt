package com.example.productscanner.workers

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.productscanner.receiver.NO_ID_SET
import com.example.productscanner.receiver.PRODUCT_ID
import com.example.productscanner.repositories.IProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateWorker @WorkerInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: IProductsRepository)
    : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // Get the product id
        val productId = inputData.getInt(PRODUCT_ID, NO_ID_SET)
        Log.d("UpdateWorker", "Data has been revert, id:".plus(productId.toString()))

        if(productId != NO_ID_SET){
            repository.revertProduct(productId)
        }

        return@withContext Result.success()
    }
}